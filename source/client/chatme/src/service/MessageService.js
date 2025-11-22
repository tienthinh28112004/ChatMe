import { toast } from "react-toastify";
import axios from "../utils/CustomizeAxios";

export const listMessageByRoom = async (roomId) => {
  try {
    const response = await axios.get(`api/rooms/${roomId}/messages`);
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Không thể lấy được danh sách tin nhắn cũ");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};

export const sendMessage = async (content,messageType,roomId,linkUrl,fileName) => {
  try {
    const response = await axios.post(`api/rooms/sendMessage`,{
        content,messageType,roomId,linkUrl,fileName
    });
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("gửi tin nhắn thất bại");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};
