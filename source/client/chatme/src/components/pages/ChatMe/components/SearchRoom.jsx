import { useState } from "react";
import "../../../css/SearchRoom.css";

export const SearchRoom = ({ onSearch }) => {
  const [value, setValue] = useState("");

  const handleChange = (e) => {
    const newKeyword = e.target.value;
    setValue(newKeyword);
    onSearch(newKeyword);
  };

  return (
    <div className="px-3 py-2">
      <div className="search-wrapper">
        <i className="bi bi-search me-2"></i>
        <input
          type="text"
          className="form-control border-0 shadow-none search-input"
          placeholder="Search"
          value={value}
          onChange={handleChange}
        />
      </div>
    </div>
  );
};
