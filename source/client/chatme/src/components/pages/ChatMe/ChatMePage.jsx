import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import {createRoom,listRoomByUserKeyword,} from "../../../service/RoomService";
import { listMessageByRoom, sendMessage } from "../../../service/MessageService";
import { ViewRoom } from "./components/ViewRoom";
import { SearchRoom } from "./components/SearchRoom";
import { getProfileInfo, getuserByKeyword } from "../../../service/UserService";
import { ViewUser } from "./components/ViewUser";
import { uploadFile } from "../../../service/ImageService";
import { ChatHeader } from "./components/ChatHeader";
import { ChatMessageList } from "./components/ChatMessageList";
import { ChatInput } from "./components/ChatInput";
import {getCentrifugoClient,initCentrifugo,subcriptionToken,getRoomOnlineCount,notifyOnline,notifyOffline,} from "../../../service/CentrifugoService";
import { joinRoom } from "../../../service/RoomMemberService";

export const ChatMePage = () => {
  const [loading, setLoading] = useState(true);
  const [rooms, setRooms] = useState([]);
  const [users, setUsers] = useState([]);
  const [activeRoom, setActiveRoom] = useState(null);
  const [messages, setMessages] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [unreadByRoom, setUnreadByRoom] = useState({});
  const [roomOnlineCount, setRoomOnlineCount] = useState(0);
  const activeRoomRef = useRef(null);
  const roomSubRef = useRef(null);
  const personalSubRef = useRef(null);

  useEffect(() => {
    activeRoomRef.current = activeRoom;
  }, [activeRoom]);

  //đây là nơi lấy room
  const fetchRooms = async () => {
    try {
      const res = await listRoomByUserKeyword("");
      setRooms(res?.result || []);
    } catch (e) {
      console.error(e);
    }
  };

  //Tìm user bằng keyword
  const fetchUser = async (keyword) => {
    if (!keyword.trim()) {
      setUsers([]);
      return;
    }
    setLoading(true);
    try {
      const res = await getuserByKeyword(keyword);
      setUsers(res?.result || []);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  //Nơi subcribe kênh cá nhân
  const subscribePersonalChannel = async (client, userId) => {
    const channel = `personal#${userId}`;
    console.log("[personal] subscribe channel =", channel);

    const token = await subcriptionToken(channel);
    if (!token) {
      console.error("[personal] không lấy được token");
      return;
    }

    const sub = client.newSubscription(channel, { token });

    //Khi có subcribe kênh cá nhân 
    sub.on("subscribed", () => {
      console.log("[personal] subscribed OK:", channel);
    });

    //Khi có người mk subcrice vừa publiccation
    sub.on("publication", (ctx) => {
      const event = ctx.data;

      if (!event || !event.type) return;

      switch (event.type) {
        //Bắt các sự kiện
        case "room_status_changed": {
          const { roomId, online, onlineCount } = event;

          setRooms((prev) =>
            prev.map((r) =>
              r.id === roomId ? { ...r, online, onlineCount } : r
            )
          );
          break;
        }
        //Bắt sự kiện thêm mesage
        case "message_added": {
          const { roomId, message } = event;

          //khi thêm mesage thì cập nhật phòng đấy lên đầu
          setRooms((prev) => {
            const arr = [...prev];
            const index = arr.findIndex((r) => r.id === roomId);
            if (index === -1) return prev;

            const room = { ...arr[index] };
            room.lastMessage = message.messageType === "FILE"
                ? "📎 Đã gửi một file"
                : message.content;
            room.lastMessageTime = message.lastMessageTime;

            arr.splice(index, 1);
            arr.unshift(room);
            return arr;
          });

          //Tăng tin nhắn chưa đọc
          setUnreadByRoom((prev) => {
            const cur = activeRoomRef.current;
            if (cur && cur.id === roomId) return prev;
            return { ...prev, [roomId]: (prev[roomId] || 0) + 1 };
          });

          break;
        }

        case "room_created": {
          const { room } = event;
          if (!room) return;

          console.log("[personal] room_created nhận:", room);

          setRooms((prev) => {
            if (prev.some((r) => r.id === room.id)) return prev; // tránh trùng
            return [room, ...prev]; // đẩy lên đầu danh sách
          });

          break;
        }

        default:
          // nếu có type khác thì log ra để xem
          console.log("[personal] unknown event type:", event.type);
          break;
      }
    });

    //nếu mình đang theo dõi người ta mà có lỗi sẽ hiện log ở đây
    sub.on("error", (err) => {
      console.error("[personal] subscription error:", err);
    });

    sub.subscribe();
    personalSubRef.current = sub;
  };

  //Khởi tạo init,là file chạy đầu tiên khi vừa vào hệ thống
  useEffect(() => {
  let mounted = true;
  let statusInterval = null;

  const init = async () => {
    try {
      const client = await initCentrifugo();//đang lấy bản thân người dùng đã được centrifugo connect
      if (!client) return;

      // RPC ping định kỳ
      statusInterval = setInterval(() => {
        client
          .rpc("update_user_status", {})
          .then((res) =>
            console.log("[RPC update_user_status interval]", res)
          )
          .catch((err) =>
            console.error("[RPC update_user_status interval] error", err)
          );
      }, 20000);

      const profile = await getProfileInfo();
      if (!mounted) return;

      if (profile?.result) {
        setCurrentUser(profile.result);
        await fetchRooms();//láy danh sách phòng 

        // subcribe cá nhân
        await subscribePersonalChannel(client, profile.result.id);

        //Khi đăng nhập gọi lệnh anyf để Be biết mình đang onlien
        await notifyOnline();
      } else {
        await fetchRooms();
      }
    } catch (e) {
      console.error(e);
    } finally {
      if (mounted) setLoading(false);
    }
  };

  init();

  return () => {
    mounted = false;
    if (statusInterval) clearInterval(statusInterval);

    //Thông báo khi logout khỏi hệ thống
    notifyOffline().catch(() => {});

    //Xóa tất cả các subcribe phòng khi logOut
    if (roomSubRef.current) {
      roomSubRef.current.unsubscribe();
      roomSubRef.current.removeAllListeners();
    }

    //Xóa tát cả subcribe cá nhân khi logout
    if (personalSubRef.current) {
      personalSubRef.current.unsubscribe();
      personalSubRef.current.removeAllListeners();
    }
  };
}, []);


  //Lựa chọn phòng
  const handleSelectRoom = async (room) => {
    if (activeRoomRef.current?.id === room.id) return;

    setActiveRoom(room);//Phòng mà mình lựa chọn
    setRoomOnlineCount(0);

    setUnreadByRoom((prev) => {
      const cp = { ...prev };
      delete cp[room.id];
      return cp;
    });

    try {
      await joinRoom(room.id);//ham này bên backend là nó được broadcast 
    } catch (e) {
      console.warn("joinRoom error (có thể đã là member):", e);
    }

    setLoading(true);
    try {
      const res = await listMessageByRoom(room.id);
      setMessages(res?.result || []);
    } finally {
      setLoading(false);
    }

    const client = getCentrifugoClient();
    if (!client) return;

    // Hủy sub phòng cũ
    if (roomSubRef.current) {
      try {
        roomSubRef.current.unsubscribe();
      } catch (e) {
        console.warn("unsubscribe old room error:", e);
      }
      try {
        roomSubRef.current.removeAllListeners();
      } catch (e) {
        console.warn("removeAllListeners old room error:", e);
      }
      roomSubRef.current = null;
    }

    const channel = `room#${room.id}`;
    let sub = client.getSubscription(channel);

    if (!sub) {
      const token = await subcriptionToken(channel);
      if (!token) return;
      //dùng để subcribe centrifugo
      sub = client.newSubscription(channel, { token });
    } else {
      try {
        sub.removeAllListeners();
      } catch (e) {
        console.warn("removeAllListeners existed sub error:", e);
      }
    }

    //Bắt sự kiện khi phòng ấy có subcribe
    sub.on("subscribed", async () => {
      try {
        const count = await getRoomOnlineCount(room.id);
        setRoomOnlineCount(typeof count === "number" ? count : 0);
      } catch (err) {
        console.error("getRoomOnlineCount error:", err);
      }
    });

    //Bắt sự kiện khi phòng ấy có người public(gửi tin nhắn,ai vào ,ai ra)
    sub.on("publication", (ctx) => {
      const message = ctx.data;

      setMessages((prev) => {
        if (prev.some((m) => m.id === message.id)) return prev;
        return [...prev, message];
      });

      setRooms((prev) =>
        prev.map((r) =>
          r.id === message.roomId? {...r,
                lastMessage:
                  message.messageType === "FILE"
                    ? "📎 Đã gửi một file"
                    : message.content,
                lastMessageTime: message.lastMessageTime,
              }
            : r
        )
      );
    });

    //Bắt sự kiên ai đó join phòng(nếu join tăng lên 1)
    sub.on("join", (ctx) => {
      console.log("Join phòng:", channel, ctx);
      setRoomOnlineCount((p) => p + 1);
    });

    //Bắt sự kiện ai đó rời khỏi phòng(nếu rời -1)
    sub.on("leave", (ctx) => {
      console.log("Leave phòng:", channel, ctx);
      setRoomOnlineCount((p) => Math.max(0, p - 1));
    });

    try {
      sub.subscribe();
    } catch (e) {
      console.warn("subscribe error:", e);
    }

    roomSubRef.current = sub;
  };

  //Gửi tin nhắn dạng text
  const handleSendText = async (content) => {
    if (!activeRoomRef.current) return;
    try {
      await sendMessage(content, "TEXT", activeRoomRef.current.id, null, "");
    } catch (err) {
      console.error("sendMessage text error:", err);
    }
  };

  //Gửi tin nhắn dạng file
  const handleSendFile = async (file) => {
    if (!activeRoomRef.current) return;

    setLoading(true);
    try {
      const res = await uploadFile(file); // { url, fileName }
      await sendMessage("","FILE",activeRoomRef.current.id,res.url,res.fileName);
    } catch (err) {
      console.error("send file error:", err);
    } finally {
      setLoading(false);
    }
  };

  //Tìm kiếm user theo keyword
  useEffect(() => {
    if (!keyword.trim()) {
      setUsers([]);
      setSelectedUserIds([]);
      return;
    }
    const to = setTimeout(() => fetchUser(keyword), 400);//sau khi nhập thông tin phải 4s sau mới tìm kiếm
    return () => clearTimeout(to);
  }, [keyword]);

  return (
    <motion.div
      initial={{ opacity: 0, x: 50 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -50 }}
      transition={{ duration: 0.5 }}
    >
      <div className="row g-0" style={{ height: "100vh", overflow: "hidden" }}>
        {/* Danh sách phòng */}
        <div
          className={"col-12 col-sm-4 col-lg-3 border-end p-0 flex-column " +
            (activeRoom ? "d-none d-sm-flex" : "d-flex")
          }
        >
          <SearchRoom onSearch={setKeyword} />
          {keyword.trim() === "" ? (
            <ViewRoom
              rooms={rooms}
              activeRoomId={activeRoom?.id}
              onSelectRoom={handleSelectRoom}
              unreadByRoom={unreadByRoom}
            />
          ) : (
            <>
              <ViewUser
                users={users}
                onToggleUser={(u) =>
                  setSelectedUserIds((prev) =>
                    prev.includes(u.id)
                      ? prev.filter((id) => id !== u.id)
                      : [...prev, u.id]
                  )
                }
                selectedUserIds={selectedUserIds}
              />
              <div className="p-2 border-top d-flex justify-content-end">
                <button className="btn btn-primary btn-sm"
                  disabled={selectedUserIds.length === 0}
                  onClick={async () => {
                    const newRoom = await createRoom(selectedUserIds);
                    if (newRoom?.result) {
                      setRooms((prev) => [newRoom.result, ...prev]);
                      setKeyword("");
                      setSelectedUserIds([]);
                      await handleSelectRoom(newRoom.result);
                    }
                  }}
                >
                  Tạo phòng ({selectedUserIds.length})
                </button>
              </div>
            </>
          )}
        </div>

        {/* Phần khung chat */}
        <div
          className={"col-12 col-sm-8 col-lg-9 p-0 d-flex flex-column " +(!activeRoom ? "d-none d-sm-flex" : "d-flex")}
        >
          {!activeRoom && (
            <div className="flex-grow-1 d-flex justify-content-center align-items-center d-none d-sm-flex">
              <p className="text-muted">💬 Hãy chọn hoặc tạo phòng</p>
            </div>
          )}

          <ChatHeader
            room={activeRoom}
            onlineCount={roomOnlineCount}
            onBackToList={() => setActiveRoom(null)}
          />

          <ChatMessageList
            currentUserId={currentUser?.id}
            messages={messages}
            loading={loading}
          />

          <ChatInput
            disabled={!activeRoom}
            onSendText={handleSendText}
            onSendFile={handleSendFile}
          />
        </div>
      </div>

    </motion.div>
  );
};
