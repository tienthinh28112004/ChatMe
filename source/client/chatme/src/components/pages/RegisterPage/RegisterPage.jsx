import { useEffect, useState } from "react"
import { Link,useNavigate } from "react-router-dom";
import { register } from "../../../service/AuthenticationService";
import { motion } from "framer-motion";
import { RegisterForm } from "./components/RegisterForm";
export const Register =() =>{
    useEffect(() => {
        document.title = "Register Page";
    });

    const [formData,setformData] = useState({
        email:"",
        password:"",
        fullName:"",
    });

    const [formErrors,setFormErrors] = useState({
        email:"",
        password:"",
        fullName:"",
    });

    const [errorMessage,setErrorMessage] = useState("");//Hiển thị thông báo lỗi
    const navigate = useNavigate();

    //Xử lý thay đổi giá trị các input
    const handleInputChange = (e) =>{
        const { name,value } = e.target;

        setformData({
            ...formData,
            [name]:value,
        });

        setFormErrors({
            ...formErrors,
            [name]:value ? "":formErrors[name],
        });
    }

    //Xử lý lỗi khi để trống
    const handleInputBlur = (event) =>{
        const {name,value} = event.target;
        if(!value){
            setFormErrors({
                ...formErrors,
                [name]:"This field cannot be left blank"
            });
        }
    };

    //Kiểm tra email và đăng ký
    const handleRegisterSubmit = async(e) =>{
        e.preventDefault();

        if(formData.password.length <6){
            setErrorMessage("Password must contain 1 uppercase letter, 1 lowercase letter, 1 special character and no spaces")
            return ;
        }
        try{
            const response=await register(formData.email,formData.password,formData.fullName);
            if(response.code === 1000){
                setErrorMessage("");
                navigate("/login");
            }else{
                setErrorMessage("tài khoản đã tồn tại");
            }
        }catch(error){
            console.log(error);
            setErrorMessage("Password must contain 1 uppercase letter, 1 lowercase letter, 1 special character and no spaces");
        }
    };

    return (
        <motion.div
            initial={{opacity:0, x:100}}//hiệu ứng ban đầu ẩn và dịch phải
            animate={{opacity: 1,x:0}} //Hiệu ứng khi hiển thị: hiện và dịch về phía vị trí gốc
            exit={{opacity: 0,x: -100}} //Hiệu ứng khi thoát: ẩn và dịch trái
            transition={{duration: 0.5}} //thời gian chuyển động
            className="content-page"
        >
            <section className="py-3 py-md-5 py-xl-8">
                <div className="container">
                    <div className="row">
                        <div className="col-12">
                            <div className="mb-5">
                                <h2 className="display-5 fw-bold text-center">Register</h2>
                                <p className="text-center m-0">
                                    Already have an account? <Link to="/login">Sign in</Link>
                                </p>
                            </div>  
                        </div>
                    </div>
                    <RegisterForm 
                        handleRegisterSubmit={handleRegisterSubmit}
                        errorMessage={errorMessage}
                        formData={formData}
                        handleInputChange={handleInputChange}
                        handleInputBlur={handleInputBlur}
                        formErrors={formErrors}
                    />
                </div>
            </section>
        </motion.div>
    )
};