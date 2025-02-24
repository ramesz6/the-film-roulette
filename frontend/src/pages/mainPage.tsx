import { useState, useEffect } from "react";
import "./App.css";
import { loadMovie } from "../api/getMovie";
import { Movies } from "../api/getMovie";

const MainPage = () => {
    function App() {

        const [error, setError] = useState("")
        const [movieDatas, setMovieDatas] = useState<Movies[] | null>()
        const [clientSearch, setClientSearch] = useState("")
        const [waitingforData, setwaitingforData] = useState(false)


        const getMovies = async () => {
            const response = await loadMovie()
            if (!response.success) {
                setError("A szerver nem elérhető")
            } else {
                if (movieDatas == null) {
                    setwaitingforData(false)
                }
                setwaitingforData(true)
                setMovieDatas(response.data)
                console.log(movieDatas)
            }
        }
        useEffect(() => {
            getMovies();
        }, []);

        return (
            <>
                {waitingforData ?

                    <div className='flex flex-wrap flex-row justify-center items-center'>
                        {movieDatas?.map((movie) => (
                            <div className="card w-96 bg-base-100 shadow-xl m-5">
                                <h1 className="card-title">{movie.title}</h1>
                                <h1 className="card-title">{movie.posterPath}</h1>
                            </div>
                        ))}
                    </div>
                    :
                    <p className="flex justify-center items-center text-center">Adatok betöltése<span className="loading loading-infinity loading-lg"></span></p>
                }
            </>
        );
    }
}