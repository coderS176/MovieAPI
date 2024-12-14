package com.movieflex.service;

import com.movieflex.dto.MovieDto;
import com.movieflex.dto.MoviePageResponse;
import com.movieflex.entity.Movie;
import com.movieflex.exceptions.MovieNotFoundException;
import com.movieflex.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    private final FileService fileService;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${project.poster}")
    private String path;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService){
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        // 1. upload the file
       if( Files.exists(Path.of(path + File.separator + file.getOriginalFilename()))) throw new FileAlreadyExistsException("File Already Exists!");
       String uploadedFileName = fileService.uploadFile(path,file);

        // 2. set the value of field poster as filename
        movieDto.setPoster(uploadedFileName);
        // 3. map dto to movie object
        Movie movie = new Movie( null ,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster());

        // 4. save the movie object -> this return saved movie object
        Movie savedMovie = movieRepository.save(movie);

        // 5. we need to return movieDto object generate the poster Url and finally map movie object to dto object and return
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        return new MovieDto(savedMovie.getMovieId(),
                                         savedMovie.getTitle(),
                                          savedMovie.getDirector(),
                                           savedMovie.getStudio(),
                                           savedMovie.getMovieCast(),
                                           savedMovie.getReleaseYear(),
                                           savedMovie.getPoster(),
                                           posterUrl);
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // 1. Check the data in DB if Exists fetch the data of given ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException("Movie not found with id: "+ movieId + "."));


            // 2. generate poster url

            String posterUrl = baseUrl + "/file/" + movie.getPoster();

            // 3. map to movieDto object and return it
            return new MovieDto(movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl);
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // 1. fetch all data from db
        List<Movie> movies = movieRepository.findAll();

        // 2. we have to iterate through the list and generate poster url for each movie object
        List<MovieDto> movieDtos = new ArrayList<>();
        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl);
            movieDtos.add(movieDto);

        }
        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. check if movie exists or not with given id
        Movie movie = movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException("Movie not found with id: "+ movieId + "."));

        // 2. if file is null do nothing with file service and if file is not_null then delete existing file and upload new file
        String fileName = movie.getPoster();
        if(file != null){
            Files.deleteIfExists(Path.of(path + File.separator + fileName));
            fileName = fileService.uploadFile(path,file);
            movie.setPoster(fileName);
        }

        // 3. set movieDto poster value according to step 2
        movieDto.setPoster(fileName);

        // 4. To map it to movie object
        Movie mv = new Movie(
                movieId,
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster()
        );
        // 5. save the movie object and it will return saved movie object
        Movie updatedMovie = movieRepository.save(mv);

        // 6. generate poster url
        String posterUrl = baseUrl + "/file/" + updatedMovie.getPoster();

        // 7. map to movieDto and return
        return new MovieDto(movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl);
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. check if movieId exists in Db
        Movie movie = movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException("Movie not found with id: "+ movieId + "."));

        // 2. delete the file associated with this object
        Files.deleteIfExists(Path.of(path,File.separator, movie.getPoster()));

        // 3. delete the movie Object
        movieRepository.deleteById(movieId);

        return "Movie Deleted with Id: "+ movieId + "!";
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl);
            movieDtos.add(movieDto);
        }
        return new MoviePageResponse(movieDtos,pageNumber,pageSize,
                                     (int) moviePages.getTotalElements(),
                                      moviePages.getTotalPages(),
                                       moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {

        Sort sort = dir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();
        List<MovieDto> movieDtos = new ArrayList<>();
        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl);
             movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos,pageNumber,pageSize,
                moviePages.getTotalPages(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }
}
