import { toast } from "react-toastify";
import axios from "../utils/CustomizeAxios";
import { Centrifuge } from "centrifuge";

let client = null;

const CENTRIFUGO_WS_URL = "ws://localhost:9000/connection/websocket";
// Sau này deploy Docker FE + Nginx thì dùng:
// const CENTRIFUGO_WS_URL = 'ws://' + window.location.host + '/connection/websocket';

// Lấy connection token từ backend
export const connectionToken = async () => {
  try {
    const response = await axios.get(`api/centrifugo/connectionToken`);
    console.log("[connectionToken]", response.data);
    if (response.data.result) {
      return response.data.result;   
    } else {
      toast.error("Không lấy được connection token");
      return null;
    }
  } catch (error) {
    console.error("Error connectionToken:", error);
    throw error;
  }
};

//Lấy subcription
export const subcriptionToken = async (channels) => {
  try {
    const response = await axios.get(`api/centrifugo/subcriptionToken`, {
      params: { channels },      
    });
    if (response.data.result) {
      return response.data.result;
    } else {
      toast.error("Lỗi kết nối kênh");
      return null;
    }
  } catch (error) {
    console.error("Error subcriptionToken:", error);
    throw error;
  }
};

//Kiểm tra xem người dùng có onlie không
export const isUserOnline = async (userId) => {
  try {
    const response = await axios.get(`api/centrifugo/isUserOnline/${userId}`);
    if (response.data.result !== undefined) {
      return response.data.result;
    } else {
      toast.error("Không thể xác định đối phương");
      return false;
    }
  } catch (error) {
    console.error("Error isUserOnline:", error);
    throw error;
  }
};

//Đếm số lượng người dùng online trong phòng
export const getRoomOnlineCount = async (roomId) => {
  try {
    const response = await axios.get(`api/centrifugo/getRoomOnlineCount/${roomId}`);
    if (response.data.result !== undefined) {
      return response.data.result;
    } else {
      toast.error("Không thể xác định số người trong phòng");
      return 0;
    }
  } catch (error) {
    console.error("Error getRoomOnlineCount:", error);
    throw error;
  }
};

//Thông báo online
export const notifyOnline = async () => {
  try {
    await axios.post("api/centrifugo/notifyOnline");
  } catch (e) {
    console.error("notifyUserOnline error:", e);
  }
};

//Thông báo offlien
export const notifyOffline = async () => {
  try {
    await axios.post("api/centrifugo/notifyOffline");
  } catch (e) {
    console.error("notifyUserOffline error:", e);
  }
};

//Khởi tại centrfugo
export const initCentrifugo = async () => {
  if (client) {
    if (client.state === "disconnected") {
      client.connect();//nếu chưa kết nối thì kết nối
    }
    return client;
  }

  const token = await connectionToken();//lấy connectionToekn dùng để vào centrifugo từ Backend
  if (!token) {
    console.error("Không có connection token, không thể connect Centrifugo");
    return null;
  }

  //Gửi lệnh đến centrifugo yêu cầu kết nối
  const centrifuge = new Centrifuge(CENTRIFUGO_WS_URL, { token });

  //Khi kết nối thành công
  centrifuge.on("connected", (ctx) => {
    console.log("Centrifugo connected", ctx);
  });

  //Khi kết nối thất bại
  centrifuge.on("disconnected", (ctx) => {
    console.log("Centrifugo disconnected", ctx);
  });

  //Khi gặp lỗi
  centrifuge.on("error", (ctx) => {         
    console.error("[Centrifugo ERROR]", ctx);
  });

  centrifuge.connect();//Nếu không có gì thì connect

  client = centrifuge;//gán lại cho biến client rồi trả ra
  return client;
};

export const getCentrifugoClient = () => client;
