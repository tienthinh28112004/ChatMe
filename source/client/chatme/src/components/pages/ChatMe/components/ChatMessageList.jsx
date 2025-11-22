import { useEffect, useRef } from "react";
import "../../../css/ChatMessageList.css";

export const ChatMessageList = ({ messages = [], loading, currentUserId }) => {
  const containerRef = useRef(null);

  const scrollToBottom = (behavior = "smooth") => {
    const el = containerRef.current;
    if (!el) return;
    el.scrollTo({ top: el.scrollHeight, behavior });
  };

  // mount lần đầu
  useEffect(() => {
    if (messages.length === 0) return;
    const id = setTimeout(() => {
      scrollToBottom("auto");
    }, 0);
    return () => clearTimeout(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // khi số lượng tin thay đổi
  useEffect(() => {
    if (messages.length === 0) return;
    const id = setTimeout(() => {
      scrollToBottom("smooth");
    }, 0);
    return () => clearTimeout(id);
  }, [messages.length]);

  return (
    <div
      ref={containerRef}
      id="chat-message-container"
      className="flex-grow-1 overflow-auto p-4 chat-message-list"
      style={{
        maxHeight: "calc(100vh - 160px)",
      }}
    >
      {loading && (
        <div className="text-center text-muted mb-2">Đang tải tin nhắn...</div>
      )}

      {messages && messages.length > 0 ? (
        messages.map((m) => {
          const isMine = m.senderId === currentUserId;
          const timeStr = m.createdAt
            ? new Date(m.createdAt).toLocaleTimeString("vi-VN", {
                hour: "2-digit",
                minute: "2-digit",
              })
            : "";

          return (
            <div
              key={m.id}
              className={
                "d-flex mb-3 " +
                (isMine ? "justify-content-end" : "justify-content-start")
              }
            >
              <div
                className={
                  "chat-bubble " +
                  (isMine ? "chat-bubble-mine" : "chat-bubble-other")
                }
              >
                {!isMine && (
                  <div className="chat-sender-name mb-1">
                    {m.senderName}
                  </div>
                )}

                {m.messageType === "FILE" && m.url ? (
                  m.url.match(/\.(jpg|jpeg|png|gif)$/i) ? (
                    <img
                      src={m.url}
                      alt={m.fileName || "attachment"}
                      className="img-fluid rounded-4"
                      style={{ maxHeight: "220px" }}
                    />
                  ) : (
                    <a
                      href={m.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className={isMine ? "text-light" : "text-primary"}
                    >
                       {m.fileName || "Tệp đính kèm"}
                    </a>
                  )
                ) : (
                  <div className="chat-bubble-text">{m.content}</div>
                )}
                <div className="text-end mt-1">
                  <small
                    className={
                      "chat-bubble-time " + (isMine ? "text-light" : "")
                    }
                  >
                    {timeStr}
                  </small>
                </div>
              </div>
            </div>
          );
        })
      ) : !loading ? (
        <div className="text-center text-muted">
          Chưa có tin nhắn nào. Hãy bắt đầu cuộc trò chuyện nhé.
        </div>
      ) : null}
    </div>
  );
};
