import Menubar from "./components/Menubar/Menubar.jsx";

const App = () => {
    return (
        <div>
            <Menubar />
            <Routes>
                <Route path = "/dashboard" element={<Dashboard/>}/>
                <Route path = "/category" element={<ManageCategory/>}/>
                <Route path = "/users" element={<ManageUsers/>}/>
                <Route path = "/items" element={<ManageItems/>}/>
                <Route path = "/explore" element={<Explore/>}/>
                <Route path = "/" element={<Dashboard/>}/>

            </Routes>
        </div>
    );
}

export default App;