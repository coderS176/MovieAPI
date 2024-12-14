package com.movieflex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflex.dto.MovieDto;
import com.movieflex.dto.MoviePageResponse;
import com.movieflex.exceptions.EmptyFileException;
import com.movieflex.service.MovieService;
import com.movieflex.utils.AppConstats;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService){
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDto) throws IOException, EmptyFileException {
        // when you are dealing with file and json object then you need to convert string to class object
        // then only you can interact with service class
        if(file.isEmpty()) throw new EmptyFileException("File is Emply! please send file.");
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto,file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId){
        return new ResponseEntity<>(movieService.getMovie(movieId),HttpStatus.OK);
    }

    @GetMapping("/all-movies")
    public ResponseEntity<Iterable<MovieDto>> getAllMoviesHandler(){
        return new ResponseEntity<>(movieService.getAllMovies(),HttpStatus.OK);
    }

    @GetMapping("/all-movies-pages")
    public ResponseEntity<MoviePageResponse> getMovieWithPagination(
            @RequestParam(defaultValue = AppConstats.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstats.PAGE_SIZE,required = false) Integer pageSize
    ){
       return new ResponseEntity<>(movieService.getAllMoviesWithPagination(pageNumber,pageSize),HttpStatus.OK);
    }

    @GetMapping("/all-movies-pages-sort")
    public ResponseEntity<MoviePageResponse> getMovieWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstats.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstats.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstats.SORT_BY,required = false) String sortBy,
            @RequestParam(defaultValue = AppConstats.SORT_DIR,required = false) String dir
    ){
        return new ResponseEntity<>(movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize,sortBy,dir),HttpStatus.OK);
    }

    @PutMapping("/update-movie/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId, @RequestPart MultipartFile file, @RequestPart String movieDto) throws IOException {
        MovieDto dto = convertToMovieDto(movieDto);
        if(file.isEmpty()) file = null;
        return new ResponseEntity<>(movieService.updateMovie(movieId,dto,file),HttpStatus.OK);
    }

    @DeleteMapping("/delete-movie/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return new ResponseEntity<>(movieService.deleteMovie(movieId),HttpStatus.OK);
    }


    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj,MovieDto.class);
    }
}
