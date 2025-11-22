import { toast } from "react-toastify";
import axios from "../utils/CustomizeAxios";


export const getUserById = async (userId) => {
  try {
    const response = await axios.post(`api/user/getUserById/${userId}`);
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Không thể lấy được thông tin user");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};

export const getProfileInfo = async () => {
  try {
    const response = await axios.get(`api/user/getMyInfo`);
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Lấy thông tin bản thân thất bại");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};

export const getuserByKeyword = async (keyword) => {
    
  try {
   const response = await axios.get("api/user/getUserByKeyword",
      { params: { keyword } } 
    );
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Không tìm thấy hợp lệ");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};
