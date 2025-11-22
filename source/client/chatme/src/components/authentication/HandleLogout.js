import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import AuthContext from "../../context/AuthContext";
import { getCentrifugoClient } from "../../service/CentrifugoService";

export const HandleLogout = () => {
  const navigate = useNavigate();
  const authContext = useContext(AuthContext);

  const handleLogout = async () => {
    const accessToken = sessionStorage.getItem("accessToken");

    // Không có token => coi như đã logout, dọn FE + quay về login
    if (!accessToken) {
      console.log("No token found");
      // Nếu refresh là optional thì dùng ?. để tránh lỗi
      authContext.refresh?.();
      navigate("/login");
      return;
    }

    try {
      // Gọi qua Nginx (khi chạy Docker + Nginx)
      const response = await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include", // gửi kèm cookie nếu có
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify({ accessToken }),
      });

      if (!response.ok) {
        try {
          const errorData = await response.json();
          console.log(
            "Logout failed:",
            errorData.message || response.statusText
          );
        } catch (_) {
          console.log("Logout failed with status:", response.status);
        }
      }
    } catch (error) {
      console.log("Logout error:", error);
    } finally {
      // Ngắt kết nối Centrifugo
      try {
        const client = getCentrifugoClient?.();
        if (client) {
          client.disconnect(); // đóng WS, hủy hết subscribe
        }
      } catch (e) {
        console.log("Centrifugo disconnect error:", e);
      }

      sessionStorage.clear();
      authContext.refresh?.();
      navigate("/login");
    }
  };

  return { handleLogout };
};
