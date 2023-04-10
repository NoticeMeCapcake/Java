package com.pet.blogproject.repo;

import com.pet.blogproject.models.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface PostRepository extends CrudRepository<Post, Long> {

}
