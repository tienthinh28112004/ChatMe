import { toast } from "react-toastify";
import axios from "../utils/CustomizeAxios";

export const uploadFile = async (file) => {
  try {
   const formData = new FormData();
    formData.append("file", file);

    const response = await axios.post("api/uploadImage", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    console.log(response.data);
    if (response.data.result) {
      return response.data.result;
    } else {
      toast.success("Upload ảnh không thành công");
      return;
    }
  } catch (error) {
    throw new Error(error);
  }
};
