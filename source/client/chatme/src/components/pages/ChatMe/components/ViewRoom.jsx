import "../../../css/ViewRoom.css";

export const ViewRoom = ({ rooms, onSelectRoom, activeRoomId, unreadByRoom }) => {
  const getLastMessagePreview = (room) => {
    // console.log(room)
    if (!room.lastMessage || room.lastMessage.trim() === "") {
      return "Chưa có tin nhắn";
    }
    return room.lastMessage;
  };

  const isRoomOnline = (room) => {
    const count = typeof room.onlineCount === "number" ? room.onlineCount : 0;
    console.log(room);
    return room.online && count > 1;
  };

  return (
    <div className="chat-sidebar h-100 d-flex flex-column">
      {rooms && rooms.length > 0 ? (
        <div className="list-group list-group-flush px-2 py-3">

          {rooms.map((room) => {
            const isActive = activeRoomId === room.id;
            const online = isRoomOnline(room);
            const unread = unreadByRoom?.[room.id] ?? room.unreadCount ?? 0;

            const lastMessageClass =
              "room-last-message" + (isActive ? "" : " text-muted");
            const lastTimeClass =
              "room-last-time" + (isActive ? "" : " text-muted");

            return (
              <button
                key={room.id}
                type="button"
                onClick={() => onSelectRoom(room)}
                className={
                  "chat-item list-group-item list-group-item-action border-0 rounded-4 mb-2 py-2 px-3 shadow-sm d-flex justify-content-between align-items-center" +
                  (isActive ? " chat-item-active" : " bg-white")
                }
              >
                {/*Khối avata,text*/}
                <div className="d-flex align-items-center">

                  {/* Avatar */}
                  <div className="position-relative me-2">
                    <img
                      src={room.avatarUrl || "user_img.jpg"}
                      alt={room.name}
                      className="chat-avatar rounded-circle"
                    />
                    {online && (
                      <span className="chat-status-dot position-absolute rounded-circle" />
                    )}
                  </div>

                  {/* Khối text (Tên + Last message) */}
                  <div className="room-info">
                    <span className="room-name fw-semibold">{room.name}</span>
                    <small className={lastMessageClass}>
                      {getLastMessagePreview(room)}
                    </small>
                  </div>
                </div>

                {/* --- khối phải --- */}
                <div className="text-end ms-2 room-meta">
                  <small className={lastTimeClass}>
                    {room.lastMessageTime || ""}
                  </small>

                  {unread > 0 && (
                    <span className="chat-unread-badge badge bg-primary rounded-pill mt-1">
                      {unread}
                    </span>
                  )}
                </div>
              </button>
            );
          })}

        </div>
      ) : (
        <div className="flex-grow-1 d-flex justify-content-center align-items-center text-muted">
          Không có phòng nào
        </div>
      )}
    </div>
  );
};
