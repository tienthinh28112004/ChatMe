import { toast } from "react-toastify";
import axios from "../utils/CustomizeAxios";

export const joinRoom = async (roomId) => {
  try {
    const response = await axios.post(`api/rooms/join/${roomId}`);
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Vào phòng thất bại");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};

export const leaveRoom = async (roomId) => {
  try {
    const response = await axios.post(`api/rooms/leave/${roomId}`);
    console.log(response.data);
    if (response.data.result) {
      return response.data;
    } else {
      toast.success("Rời phòng thất bại");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};
