import { useState } from "react";
import "../../../css/ChatInput.css";

export const ChatInput = ({ disabled, onSendText, onSendFile }) => {
  const [text, setText] = useState("");
  const [uploading, setUploading] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    const value = text.trim();
    if (!value || disabled || uploading) return;
    onSendText(value);
    setText("");
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      setUploading(true);
      const url = await onSendFile(file);
      console.log(url);
      console.log("Uploaded file URL:", url?.result);
    } catch (error) {
      console.error("Upload file error:", error);
      alert("Tải file thất bại!");
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  };

  const isDisabled = disabled || uploading;

  return (
    <form
      className="border-top bg-white px-3 py-2"
      onSubmit={handleSubmit}
    >
      <div className="chat-input-wrapper d-flex align-items-center">
        {/* Nút upload file */}
        <label className="mb-0 me-2 chat-input-icon-btn">
          <i className="fa fa-paperclip" />
          <input
            type="file"
            className="d-none"
            disabled={isDisabled}
            onChange={handleFileChange}
          />
        </label>

        {/* Ô nhập text */}
        <input
          type="text"
          className="flex-grow-1 border-0 bg-transparent chat-input-field"
          placeholder={
            uploading ? "Đang tải file..." : "Type a message here..."
          }
          value={text}
          onChange={(e) => setText(e.target.value)}
          disabled={isDisabled}
        />

        {/* Icon emoji */}
        <button
          type="button"
          className="chat-input-icon-btn ms-2"
          disabled={isDisabled}
        >
          <i className="fa-regular fa-face-smile" />
        </button>

        {/* Icon mic */}
        <button
          type="button"
          className="chat-input-icon-btn ms-2"
          disabled={isDisabled}
        >
          <i className="fa fa-microphone" />
        </button>

        {/* Nút gửi gradient tròn */}
        <button
          type="submit"
          className="chat-send-btn ms-2"
          disabled={isDisabled || !text.trim()}
        >
          <i className="fa fa-paper-plane" />
        </button>
      </div>
    </form>
  );
};
