import { toast } from "react-toastify";
import axios from "../utils/CustomizeAxios";

export const login = async (email, password) => {
  try {
    const response = await axios.post(`api/auth/login`, {
      email,
      password,
    });
    console.log(response.data.result);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Tài khoản hoặc mật khẩu không đúng");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};

export const introspect = async () => {

  try {
    const accessToken = sessionStorage.getItem("accessToken");
    if(!accessToken){
        throw new Error("Token is missing");
    }

    const response = await axios.post(`api/auth/introspect`,{
        accessToken: accessToken
    });
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Không thể xác minh người dùng");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};

export const register = async (email,password,fullName) => {
  try {
    const response = await axios.post(`api/auth/register`, {
        fullName,
        email,
        password,
    });
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Đăng ký thành viên thất bại");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};
