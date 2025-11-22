import { createContext, useEffect, useState } from "react";
import {introspect} from "../service/AuthenticationService";

const AuthContext = createContext({});


export const AuthProvider =({children}) => {
    const [authenticated,setAuthenticated] = useState(false);

    const refresh = async()=>{
        const accessToken = sessionStorage.getItem("accessToken");
        console.log(accessToken);
        if(!accessToken){
            setAuthenticated(false);
            return ;
        }
        try{
            const result = await introspect();
            console.log(result);
        }catch(error){
            console.error("Error introspecting token:",error);
            setAuthenticated(false);
        }
    }
    
    useEffect(()=>{
        refresh();
    },[]);
    return (
        <AuthContext.Provider value={{authenticated,refresh}}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;