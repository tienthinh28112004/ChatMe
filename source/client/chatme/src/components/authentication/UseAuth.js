import { useEffect, useState } from "react"

export const useAuth = ({loggedOut})=>{
    const [isTokenValid,setIsTokenValid] = useState(null);
    const accessToken =sessionStorage.getItem('accessToken');
    const accessTokenExpiry = parseInt(sessionStorage.getItem('accessTokenExpiry'),10);//chuyển đổi thời gian sống token về dạng int(thập phân)(mình để là 60 phút:))
    //refreshToken mỗi khi gần hết hạn
    useEffect(()=>{
        const checkAccessToken = async()=>{
            //nếu không có token hoặc đã đăng xuất,đánh dấu không hợp lệ
            if(!accessToken || isNaN(accessTokenExpiry)||loggedOut){
                setIsTokenValid(false);//đánh dấu không hợp lệ
                return ;
            }

            const now = new Date().getTime();
            const timeLeft = accessTokenExpiry - now;//lấy thời gian còn lại của token
            const fiveMinutesInMs = 5*60*1000;//5 phút bằng milisecond

            //nếu accessToken sắp hết hạn(cụ thể là 5 phút),thực hiện bằng refresh
            if(timeLeft < fiveMinutesInMs){
                try{
                    const response = await fetch('http://localhost:9000/api/auth/refresh',{
                        method: 'POST',
                        credentials: 'include',//quan trọng:gửi kèm cookies
                        headers:{
                            'Content-Type':'application/json'
                        }
                    });
                    
                    if(!response.ok) throw new Error('Failed to refresh token');

                    const data = await response.json();
                    const hour = 60*60*1000;//lấy thời gian tồn tại là 1 giờ(tương tự thời gian tồn tại để trong accessToken)
                    const newExpiryTime = new Date().getTime() + hour;

                    sessionStorage.setItem('accessToken',data.result.accessToken);
                    sessionStorage.setItem('accessTokenExpiry',newExpiryTime.toString());

                    setIsTokenValid(true);//đánh dấu hợp lệ
                }catch(error){
                    console.error('Error refreshing token:',error);
                    setIsTokenValid(false);
                }
            }else{
                setIsTokenValid(true);//đánh dấu hợp kệ
            }
        };
        checkAccessToken();
    },[accessToken,accessTokenExpiry,loggedOut]);

    useEffect(() =>{
        const introspectToken = async ()=>{
            //nếu không có token hoặc đã đăng xuất thì không introsepct nữa
            if(!accessToken || loggedOut){
                setIsTokenValid(false);//đánh dấu không hợp lệ
                return ;
            }

            try{
                const response = await fetch('http://localhost:9000/api/auth/introspect',{
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`
                    },
                    body: JSON.stringify({accessToken: accessToken})
                });

                if(!response.ok) throw new Error('Token introspection failed');

                const data = await response.json();
                setIsTokenValid(data.result.valid);//đánh dấu trạng thái hợp lệ
            }catch(error){
                console.log('Error introspect token:',error);
                setIsTokenValid(false);
            }
        };
        introspectToken();
    },[accessToken,loggedOut]);
    return { isTokenValid };//trả về thông tin hợp lệ hay không
};