import { useNavigate } from "react-router-dom"

export const NotFound = () =>{
    const navigate = useNavigate();

    const handleGoBack=() =>{
        navigate('/');
    };

    return (
        <div className="not-found-container">
            <h1 className="not-found-title">404</h1>
            <h2 className="not-found-message">Oops! The page you're looking for doesn't exist.</h2>
            <p className="not-found-description">
                It seems that you have taken a wrong turn. Don't worry... it happens to the best of us.
            </p>
            <button className="go-back-button" onClick={handleGoBack}>
                Go Back to Home
            </button>
        </div>
    )
}