package com.movieflex.service;

import com.movieflex.dto.MovieDto;
import com.movieflex.entity.Movie;
import com.movieflex.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        String uploadedFileName = fileService.uploadFile(path,file);

        // 2. set the value of field poster as filename
        movieDto.setPoster(uploadedFileName);
        // 3. map dto to movie object
        Movie movie = new Movie( movieDto.getMovieId(),
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
        return null;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        return List.of();
    }
}
