/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom"

export const ProcessloginOAuth2 = ()=>{
    const navigate= useNavigate();
    const pathParams = useParams();
    const [query] = useSearchParams();

    useEffect(()=>{
        const clientCode = pathParams.clientCode;
        function handleAuthentication(){
            const authCodeRegex = /code=([^&]+)/;//tạo ra regex để lấy mã code mà google gửi về
            const isMatch = window.location.href.match(authCodeRegex);//lấy mã trên google trả về rồi giải mã ra code

            if(isMatch){//nếu như tìm thấy thì gửi trả về backend cái code
                const authCode = isMatch[1];//lấy ra mã code trong cái google trả về

                fetch(`http://localhost:9000/api/auth/outbound/authentication?code=${authCode}`,{
                    method: "POST",
                })
                .then((response)=>response.json())
                .then((data)=>{
                    if(data.result && data.result.accessToken){

                        sessionStorage.setItem("accessToken",data.result.accessToken);

                        fetch(`http://localhost:9000/api/user/getMyInfo`, {
                            method: "GET",
                            headers: {
                              "Content-Type": "application/json",
                              Authorization: `Bearer ${data.result.accessToken}`,
                            },
                          })
                            .then((response) => response.json())
                            .then((userData) => {
                                window.location.href = "/"; // Điều này sẽ làm mới trang
                            });
                        navigate("/");
                    }
                })
                .catch((error)=>{
                    console.log("Error:",error);
                });
            }
        }
        handleAuthentication();
    },[]);

    return (
        <div className="container text-center">
            <div className="d-flex justify-content-center align-items-center flex-column">
                <div className="spinner-border spinner-border-custom text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
                <h2 className="mt-3">Processing...</h2>
                <p className="text-muted">Please wait while we authenticate your account.</p>
            </div>
            <style>
                {`
                    body{
                        background-color:#f8f9fa;
                        display: flex;
                        justify-content:center;
                        align-items:center;
                        height:100vh;
                        margin: 0;
                    }
                    .spinner-border-custom{
                        width: 4rem;
                        height: 4rem;
                        border-width: 0.4em;
                    }
                `}
            </style>
        </div>
    );
};