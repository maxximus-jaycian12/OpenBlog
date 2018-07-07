package com.unloadbrain.blog.config.modelmapper;

import com.unloadbrain.blog.domain.model.Category;
import com.unloadbrain.blog.domain.model.Tag;
import com.unloadbrain.blog.dto.CreateUpdatePostRequest;
import com.unloadbrain.blog.dto.PostDTO;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostModelMapperMapping implements ModelMapperMapping {

    @Override
    public void appendToModelMapper(ModelMapper modelMapper) {

        addMappingCreateUpdatePostRequestToPostDTO(modelMapper);
        addMappingCategoriesTagsToCommaSeparatedString(modelMapper);
    }

    private ModelMapper addMappingCategoriesTagsToCommaSeparatedString(ModelMapper mapper) {

        Converter<Set, String> convertCategoriesAndTags = getCategoriesTagsConverter();

        mapper.createTypeMap(Set.class, String.class).setConverter(convertCategoriesAndTags);

        mapper.validate();

        return mapper;
    }


    private Converter<Set, String> getCategoriesTagsConverter() {

        // TODO:: Find better solution

        return mappingContext -> {

            Set<Category> categories = null;
            Set<Tag> tags = null;

            Set categoriesOrTags = mappingContext.getSource();
            for (Object obj : categoriesOrTags) {
                if (obj instanceof Category) {
                    categories = (Set<Category>) categoriesOrTags;
                    break;
                } else if (obj instanceof Tag) {
                    tags = (Set<Tag>) categoriesOrTags;
                    break;
                }
            }

            if (categories != null) {
                List<String> catStringList = categories.stream().map(Category::getName).collect(Collectors.toList());
                return StringUtils.join(catStringList, ",");
            }

            if (tags != null) {
                List<String> tagStringList = tags.stream().map(Tag::getName).collect(Collectors.toList());
                return StringUtils.join(tagStringList, ",");
            }

            return null;
        };
    }


    private ModelMapper addMappingCreateUpdatePostRequestToPostDTO(ModelMapper mapper) {

        PropertyMap propertyMap = new PropertyMap<CreateUpdatePostRequest, PostDTO>() {

            @Override
            protected void configure() {
                skip(destination.getCreatedAt());
                skip(destination.getUpdatedAt());
            }
        };

        mapper.createTypeMap(CreateUpdatePostRequest.class, PostDTO.class)
                .addMapping(source -> source.getId(), PostDTO::setId)
                .addMapping(source -> source.getTitle(), PostDTO::setTitle)
                .addMapping(source -> source.getContent(), PostDTO::setContent)
                .addMapping(source -> source.getSummary(), PostDTO::setSummary)
                .addMapping(source -> source.getCategories(), PostDTO::setCategories)
                .addMapping(source -> source.getTags(), PostDTO::setTags)
                .addMapping(source -> source.getPermalink(), PostDTO::setPermalink)
                .addMapping(source -> source.getFeatureImageLink(), PostDTO::setFeatureImageLink)
                .addMappings(propertyMap);

        mapper.validate();

        return mapper;
    }

}
