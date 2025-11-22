import { toast } from "react-toastify";
import axios from "../utils/CustomizeAxios";

export const listRoomByUserKeyword = async (keyword) => {
  try {
    const params = new URLSearchParams();
    params.append('keyword',keyword);
    const response = await axios.get(`api/room/listRoom`);
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Không thể lấy được danh sách phòng");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};

export const createRoom = async (listUserId) => {
  try {
    const response = await axios.post(`api/room/addRoom`,{
        ids:listUserId
    });
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("tạo phòng thất bại");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};
