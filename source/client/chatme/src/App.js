import {NotFound} from './components/error/NotFound';
import {Accessdenied} from './components/error/Accessdenied';
import { Route, Routes } from 'react-router-dom';
// import 'font-awesome/css/font-awesome.min.css';
import './App.css';
import { LoginPage } from './components/pages/LoginPage/LoginPage';
import { Register } from './components/pages/RegisterPage/RegisterPage';
import { ProcessloginOAuth2 } from './components/authentication/OAuth2';
import { ChatMePage } from './components/pages/ChatMe/ChatMePage';
function App() {
  return (
    <div className="App">
      <Routes>
        {/* <Route path="/" element={<HeaderAndFooterRouter />}> */}
          <Route path="/" element={<ChatMePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<Register />} />
         {/* </Route>  */}
        <Route
          path="/oauth2/callback/:clientCode"
          element={<ProcessloginOAuth2 />}
        />
        <Route path="/accessdenied" element={<Accessdenied />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
}

export default App;