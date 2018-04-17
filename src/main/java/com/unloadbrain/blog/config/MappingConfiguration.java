package com.unloadbrain.blog.config;

import com.unloadbrain.blog.domain.model.Category;
import com.unloadbrain.blog.domain.model.Post;
import com.unloadbrain.blog.domain.model.PostStatus;
import com.unloadbrain.blog.domain.model.Tag;
import com.unloadbrain.blog.dto.PostDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class MappingConfiguration {

    @Bean
    public ModelMapper createModelMapper() {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        addMappingPostToPostDTO(mapper);
        addMappingPostDTOToPost(mapper);

        return mapper;
    }

    private ModelMapper addMappingPostToPostDTO(ModelMapper mapper) {

        mapper.createTypeMap(Post.class, PostDTO.class)
                .addMapping(source -> source.getId(), PostDTO::setId)
                .addMapping(source -> source.getTitle(), PostDTO::setTitle)
                .addMapping(source -> source.getContent(), PostDTO::setContent)
                .addMapping(source -> source.getPermalink(), PostDTO::setPermalink)
                .addMapping(source -> source.getFeatureImageLink(), PostDTO::setFeatureImageLink);


        mapper.addMappings(new PropertyMap<Post, PostDTO>() {

            Converter<Set<Category>, String> convertCategories = ctx ->
                    ctx.getSource() == null ? null :
                            ctx.getSource().stream().map(Category::getName).collect(Collectors.joining(","));

            Converter<Set<Tag>, String> convertTags = ctx ->
                    ctx.getSource() == null ? null :
                            ctx.getSource().stream().map(Tag::getName).collect(Collectors.joining(","));

            Converter<PostStatus, String> convertPostStatus =
                    ctx -> ctx.getSource() == null ? null : ctx.getSource().name().toUpperCase();

            @Override
            protected void configure() {
                using(convertCategories).map(source.getCategories(), destination.getCategories());
                using(convertTags).map(source.getTags(), destination.getTags());
                using(convertPostStatus).map(source.getStatus(), destination.getStatus());
                skip(destination.getAction());
            }
        });

        mapper.validate();

        return mapper;
    }

    private ModelMapper addMappingPostDTOToPost(ModelMapper mapper) {

        mapper.createTypeMap(PostDTO.class, Post.class)
                .addMapping(source -> source.getId(), Post::setId)
                .addMapping(source -> source.getTitle(), Post::setTitle)
                .addMapping(source -> source.getContent(), Post::setContent)
                .addMapping(source -> source.getPermalink(), Post::setPermalink)
                .addMapping(source -> source.getFeatureImageLink(), Post::setFeatureImageLink);


        mapper.addMappings(new PropertyMap<PostDTO, Post>() {

            Converter<String, Set<Category>> convertCategories = ctx ->
                    ctx.getSource() == null ? null : Arrays.stream(ctx.getSource().split(","))
                            .map(cat -> new Category(){{setName(cat);}})
                            .collect(Collectors.toCollection(LinkedHashSet<Category>::new));

            Converter<String, Set<Tag>> convertTags = ctx ->
                    ctx.getSource() == null ? null : Arrays.stream(ctx.getSource().split(","))
                            .map(tag -> new Tag(){{setName(tag);}})
                            .collect(Collectors.toCollection(LinkedHashSet<Tag>::new));

            Converter<String, PostStatus> convertPostStatus =
                    ctx -> ctx.getSource() == null ? null : PostStatus.valueOf(ctx.getSource());

            @Override
            protected void configure() {
                using(convertCategories).map(source.getCategories(), destination.getCategories());
                using(convertTags).map(source.getTags(), destination.getTags());
                using(convertPostStatus).map(source.getStatus(), destination.getStatus());
                skip(destination.getCreatedAt());
                skip(destination.getUpdatedAt());
                skip(destination.getPublishDate());
            }
        });

        mapper.validate();

        return mapper;
    }

}