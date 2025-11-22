import axios from "axios";
let refreshingFunc = undefined;

const instance = axios.create({
    baseURL: 'http://localhost:9000/',//8080
    withCredentials: true, //cookie Http-only
});

const publicEndpoints = [
    "**"
];


const refreshToken = async () => {
    try{
        const response = await instance.post('api/auth/refresh');//không gửi token,cookie tự động gửi
        if(response.status === 200){
            const accessToken = response.data.result.accessToken;
            sessionStorage.setItem("accessToken", accessToken);//refesh thành công lưu vào sessionStorage
            return accessToken;
        } else {
            throw new Error('Failed to refresh token');
        }
    } catch(error) {
        console.error("Error refreshing token: ", error);
        throw error;
    }
};

instance.interceptors.request.use(
    (config) => {
        const accessToken = sessionStorage.getItem('accessToken');
        
        // Chỉ thêm token vào header nếu:
        // 1. Có accessToken
        // 2. URL không phải là endpoint refresh token
        // 3. URL không phải là public endpoint
        if (accessToken && config.url !== 'api/auth/refresh') {
            config.headers['Authorization'] = `Bearer ${accessToken}`;
        }
        
        return config;
    },
    (error) => {
        return error.response ? error.response : Promise.reject(error);
    }
);

instance.interceptors.response.use(
    (response) => {
        console.log(response);
        return response;//nếu api phản hồi thành công status !=401 thì trả về response như bình thường
    },
    async (error) => {
        const originalRequest = error.config;
        console.log(originalRequest);
        // Nếu là public endpoint và gặp lỗi 401, chỉ trả về lỗi mà không redirect
        if (error.response && error.response.status === 401) {
            return Promise.reject(error);
        }
        console.log(originalRequest);
        // Xử lý refresh token cho các endpoint yêu cầu authentication
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            
            if (!refreshingFunc) {
                refreshingFunc = refreshToken();
            }
            
            try {
                const newToken = await refreshingFunc;
                sessionStorage.setItem('accessToken', newToken);
                
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
                refreshingFunc = undefined;
                
                return instance(originalRequest);
            } catch (refreshError) {
                refreshingFunc = undefined;
                sessionStorage.removeItem("accessToken");
                
                // Chỉ redirect đến login nếu không phải là public endpoint
                // if (!isPublicEndpoint(originalRequest.url)) {
                //     window.location = `${window.location.origin}/login`;
                // }
                
                return Promise.reject(refreshError);
            }
        }
        
        return Promise.reject(error);
    }
);

export default instance;