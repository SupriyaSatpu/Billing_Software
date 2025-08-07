import { Toaster } from "react-hot-toast";
import Menubar from "./components/Menubar/Menubar.jsx";
import { Route, Routes } from "react-router-dom";
import Dashboard from "./Pages/Dashboard/Dashboard.jsx";
import ManageCategory from "./Pages/ManageCategory/ManageCategory.jsx";
import ManageUsers from "./Pages/ManageUsers/ManageUsers.jsx";
import ManageItems from "./Pages/ManageItems/ManageItems.jsx";
import Explore from "./Pages/Explore/Explore.jsx";
import {Toaster} from "react-hot-toast";
import Login from "./Pages/Login/Login.jsx";
import OrderHistory from "./Pages/OrderHistory/OrderHistory.jsx";


const App = () => {
    const location = useLocation();
    return (
        <div>
            {location.pathname !== "/login" && <Menubar/>}
            <Toaster/>
            <Routes>
                <Route path = "/dashboard" element={<Dashboard/>}/>
                <Route path = "/category" element={<ManageCategory/>}/>
                <Route path = "/users" element={<ManageUsers/>}/>
                <Route path = "/items" element={<ManageItems/>}/>
                <Route path = "/explore" element={<Explore/>}/>
                <Route path = "/" element={<Dashboard/>}/>
                <Route path = "/login" element={<Login/>}/>
                <Route path = "/orders" element={<OrderHistory/>}/>

            </Routes>
        </div>
    );
}

export default App;