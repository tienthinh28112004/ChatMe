import { useContext, useEffect, useState } from "react";
import { introspect } from "../service/AuthenticationService";
import AuthContext from "../context/AuthContext";

export const useAuthData = () => {
    const authContext = useContext(AuthContext)
    const [loading, setLoading] = useState(true);
  
    useEffect(() => {
      if (!authContext.authenticated) {
        console.log("User not authenticated");
        setLoading(false);
        return;
      }
  
      introspect()
        .then((data) => {
          if (data.valid) {
            console.log("Fetched roles:", data.valid);
          }
          setLoading(false);
        })
        .catch(() => {
          setLoading(false);
        });
    }, [authContext]);
  
    return { loading };
  };
  