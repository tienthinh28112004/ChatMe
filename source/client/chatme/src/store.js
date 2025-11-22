import { legacy_createStore as createStore } from "redux";
//file cấu hình redux
const initialState = {
    sidebarShow: true, //xác định trạng thái ban đầu của sidebar(có hiển thị hay không)
    theme: "light", //xác định theme mặc định của ứng dụng (chế độ sáng)
};

const changeState = (state = initialState, {type, ...rest}) =>{
    switch (type) {
        case "set":
            //cập nhật trạng thái,nếu action có type là "set" nó sẽ cập nhật trạng thái bằng cách merge(...rest) vào state hiện tại
            return {...state,...rest};
        default:
            return state;
    }
};

const store = createStore(changeState);//tạo 1 redux store với reducer là changeState
export default store;
//quản lý trạng thái,chp phép các thành phần(components) trong ứng dụng react truy cập vào state 1 cách tập trung thông qua redux