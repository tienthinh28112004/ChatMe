import { useContext, useEffect, useState } from "react"
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import {introspect, login} from "../../../service/AuthenticationService"
import {OAuthConfig} from "../../config/OAuthConfig";
import {LoginForm} from "./components/LoginForm";
import { ToastContainer } from "react-toastify";
import AuthContext from "../../../context/AuthContext";

export const LoginPage = () =>{
    const authContext = useContext(AuthContext);
    const [email,setEmail] = useState("");
    const [password,setPassword] = useState("");
    const [showToast,setShowToast] = useState(false);
    const navigate = useNavigate();
    const [loading,setLoading] = useState(true);
    const [errorMessage,setErrorMessage]=useState("");

    useEffect(()=>{
        document.title = "Login Page";
    },[]);

    useEffect(() =>{
        const accessToken = sessionStorage.getItem("accessToken");

        if(accessToken){
            introspect()
                .then((data) =>{
                    if(data.valid){
                        navigate("/")//nếu token hợp lệ đi đến trang home
                    }else{
                        setLoading(false);//không có token thì tắt trạng thái loading đi hiển thị form đăng nhập
                    }
                })
                .catch((error) =>{
                    console.error("Error introspecting token:",error);
                    setLoading(false);//xác minh lỗi thì cũng hiển thị form đăng nhập
                });
        }else{
            setLoading(false);//không có token thì cũng hiển thị form đăng nhập
        }
    },[navigate])


    //hàm này dùng để hiển thị thông báo sau 3 giây
    useEffect(() =>{
        if(showToast){
            const timer = setTimeout(() =>setShowToast(false),3000);//sau 3 giây tự động chuyển showToast=false
            return () =>clearTimeout(timer);//thực hiện xong xuống thì xóa luôn đi
        }
    },[showToast]);

    if(loading){
        //vào mặc định sẽ hiện cái này nếu xác minh thành công(đi đến trang chủ),xác minh thất bại(hiện form đăng nhập)
        return <div>Loading....</div>
    }

    const handleGoogleLogin = () =>{
        const callbackUrl = OAuthConfig.google.redirectUri;//"http://localhost:3000/oauth2/callback/google"
        const authUrl = OAuthConfig.google.authUri;//"https://accounts.google.com/o/oauth2/auth"
        const googleClientId = OAuthConfig.google.clientId;//"736823866965-3h27864u4op3cojn478q2cspvqlpuv3o.apps.googleusercontent.com"

        const targetUrl = `${authUrl}?redirect_uri=${encodeURIComponent(
            callbackUrl
        )}&response_type=code&client_id=${googleClientId}&scope=openid%20email%20profile`;

        window.location.href=targetUrl;//trình duyệt ngay lập tức điều hướng đên targetUrl
    }

    const handleLogin = (event) =>{//email,
        event.preventDefault();//tắt trạng thái loading mặc định khi submit form
        console.log(email,password);
        if(password.length <6 ){
            setErrorMessage("Password must contain 1 uppercase letter, 1 lowercase letter, 1 special character and no spaces")
            return ;
        }
        login(email, password)
            .then((data) =>{
                if(data&&data.result.accessToken){
                    console.log(data.result.accessToken);
                    const accessToken = data.result.accessToken;
                    sessionStorage.setItem("accessToken",accessToken);

                    introspect()//không cần truyền gì vì nó tự lấy trên sessionStorage
                        .then((introspectData) =>{
                            console.log(introspectData.result.valid);
                            if(introspectData && introspectData.result&&introspectData.result.valid){
                                authContext.refresh();
                                navigate("/");
                            }else{
                                throw new Error("Invalid token.");
                            }
                        })
                        .catch((error)=>{
                            console.error("Error during introspect:",error);
                            setShowToast(true)//hiển thị thông báo
                            setTimeout(() =>setShowToast(false),4000);//tắt sau 4 giây
                        });
                }else{
                    throw new Error("Login failed,please try again.");
                }
            })
            .catch((error)=>{
                console.error("Login error:",error.message);
                setErrorMessage("Incorrect password or account. Please log in again.");
                setShowToast(true);
                setTimeout(() => setShowToast(false),4000);
            })
    };
    return (
        <motion.div
            initial={{ opacity:0,x:-100 }} //Hiệu ứng ban đầu: ẩn và dịch trái
            animate={{ opacity:1,x:0 }} //Hiệu ứng khi hiển thị: hiển thị và dịch về phí gốc
            exit={{ opacity:0,x:100}} //Hiệu ứng khi thoát: ẩn và dịch phải
            transition={{duration: 0.5}} //thời gian chuyển động
            className="content-page"
        >
            <section className="py-3 py-md-5 py-xl-8">
                <LoginForm //truyền các biến này sang loginForm để cập nhật các giá trị và rồi đưa về đây xử lý
                    email={email}
                    setEmail={setEmail}
                    password={password}
                    errorMessage={errorMessage}
                    setPassword={setPassword}
                    handleLogin={handleLogin}
                    handleGoogleLogin={handleGoogleLogin}
                />
                {/* dùng thư viện toastContainer để hiển thị thông báo */}
                <ToastContainer position="top-right" autoClose={3000} className="custom-toast-container"/>
            </section>
        </motion.div>
    )
}