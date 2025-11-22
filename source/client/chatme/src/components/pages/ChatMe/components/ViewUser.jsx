export const ViewUser = ({ users, selectedUserIds, onToggleUser }) => {
  return (
    <div className="row">
      {users.length > 0 ? (
        users.map((user) => {
          const isSelected = selectedUserIds.includes(user.id);
          console.log(user)
          return (
            <button
              key={user.id}
              type="button"
              onClick={() => onToggleUser(user)}
              className={
                "list-group-item list-group-item-action border-0 d-flex align-items-center gap-2 px-3 py-2 " +
                (isSelected ? "active" : "")
              }
              style={{ cursor: "pointer" }}
            >
              {/* Avatar */}
              <img
                src={user.avatarUrl || "user_img.jpg"}
                alt={user.userName}
                className="rounded-circle"
                style={{
                  width: "40px",
                  height: "40px",
                  objectFit: "cover",
                  border: isSelected ? "2px solid #0d6efd" : "2px solid transparent",
                }}
              />

              {/* Thông tin user */}
              <div className="d-flex flex-column text-start flex-grow-1">
                <span className="fw-semibold text-truncate">
                  {user.userName}
                </span>
                <small className="text-muted">
                  {user.online ? "Online" : "Offline"}
                </small>
              </div>

              {/* Nếu đang chọn thì hiển thị dấu ✓ */}
              {isSelected && (
                <span className="badge bg-primary rounded-pill">✓</span>
              )}
            </button>
          );
        })
      ) : (
        <div className="p-3 text-muted">Không tìm thấy người dùng nào.</div>
      )}
    </div>
  );
};
