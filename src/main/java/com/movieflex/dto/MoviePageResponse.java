package com.movieflex.dto;

import com.movieflex.entity.Movie;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDtos,
                                Integer pageNumber,
                                Integer pageSize,
                                int totalElements,
                                int totalPages,
                                boolean islast) {
}
