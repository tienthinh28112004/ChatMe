/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useState } from "react";
import {Link, useNavigate} from 'react-router-dom';
import { MdErrorOutline } from 'react-icons/md'; 

export const Accessdenied = ()=>{
    const [countdown,setCountdown]=useState(10);//Đếm ngược từ 10 giây
    const navigate = useNavigate();

    useEffect(()=>{
        const interval = setInterval(()=>{
            setCountdown(prevCountdown =>{
                if(prevCountdown<=1){
                    clearInterval(interval);
                    navigate('/home');//,đến khi về 0 thì đưa về trang home
                }
                return prevCountdown-1;
            })
        },1000);//cập nhật mỗi giây

        return ()=>clearInterval(interval);//môi giây lại trừ 1
    },[navigate]);

    return (
        <div className="forbiden-container">
            <MdErrorOutline className="forbiden-icon" size={60}/>
            <h1 className="forbiden-title">403</h1>
            <h2 className="forbidden-message">Forbiden - You don't have permission to access this page.</h2>
            <p className="forbiden-description">
                Sorry, it seems like you are not allowed to view this content. Please contact the administrator if you believe this is a mistake.
            </p>
            <p className="countdown-text">You will be redirected to the homepage in {countdown} seconds...</p>
            <Link to='/' className="go-back-button">
                Go Back to Home
            </Link>
        </div>
    );
};

