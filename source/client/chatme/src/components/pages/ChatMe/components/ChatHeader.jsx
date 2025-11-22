import { Link } from "react-router-dom";
import { HandleLogout } from "../../../authentication/HandleLogout";
import "../../../css/ChatHeader.css";

export const ChatHeader = ({ room, onlineCount }) => {
  const { handleLogout } = HandleLogout();
  if (!room) return null;

  const isDirect = room.roomType === "DIRECT";

  // Ưu tiên onlineCount realtime từ FE
  const effectiveOnlineCount =
    typeof onlineCount === "number"
      ? onlineCount
      : typeof room.onlineCount === "number"
      ? room.onlineCount
      : 0;

  const roomOnline = effectiveOnlineCount > 1;

  let statusText = "";

  if (isDirect) {
    statusText = roomOnline &&onlineCount>1
      ? "Đang hoạt động"
      : room.lastSeenText || "Last seen recently";
  } else {
    // GROUP: hiện số người online, nhưng vẫn theo rule ">=1 là online"
    statusText = roomOnline
      ? `${effectiveOnlineCount -1} thành viên khác đang online`
      : `${effectiveOnlineCount -1>0?effectiveOnlineCount:0} thành viên khác trong phòng`;
  }

  return (
    <div className="chat-header d-flex justify-content-between align-items-center flex-wrap px-3 py-2 bg-white">
      {/* avatar + tên + trạng thái */}
      <div className="chat-header-left d-flex align-items-center gap-2 flex-grow-1 overflow-hidden">
        <div className="chat-avatar-wrapper">
          <img
            src={room.avatarUrl || "user_img.jpg"}
            alt={room.name}
            className="rounded-circle"
            style={{ width: "40px", height: "40px", objectFit: "cover" }}
          />

          {roomOnline && <span className="chat-avatar-online-dot"></span>}
        </div>
        <div className="d-flex flex-column overflow-hidden">
          <span
            className={
              "chat-header-name text-truncate" +
              (roomOnline ? " chat-header-name-online" : "")
            }
          >
            {room.name}
          </span>
          <small className="chat-header-status text-truncate">
            {statusText}
          </small>
        </div>
      </div>

      {/* icon bên phải: call / video / more + logout */}
      <div className="d-flex align-items-center gap-3 chat-header-actions flex-shrink-0">
        <button
          type="button"
          className="btn btn-sm btn-link p-0 border-0"
        >
          <i className="bi bi-telephone-fill" />
        </button>
        <button
          type="button"
          className="btn btn-sm btn-link p-0 border-0"
        >
          <i className="bi bi-camera-video-fill" />
        </button>
        <button
          type="button"
          className="btn btn-sm btn-link p-0 border-0"
        >
          <i className="bi bi-three-dots-vertical" />
        </button>
        <button
          className="chat-logout-btn"
          onClick={handleLogout}
        >
          <i className="fa fa-sign-out"></i>
          Logout
        </button>
      </div>
    </div>
  );
};
