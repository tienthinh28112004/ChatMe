const Loading = ({ isLoading }) => {
  return (
    <div>
      {isLoading && (
        <div className="d-flex align-items-center justify-content-center mt-2">
          <div 
            className="spinner-border" 
            role="status"
            style={{ color: "#3b82f6" }} // Equivalent to text-blue-500 in Tailwind
          >
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      )}
    </div>
  );
};

export default Loading;