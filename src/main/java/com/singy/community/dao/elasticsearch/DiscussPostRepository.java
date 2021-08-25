package com.singy.community.dao.elasticsearch;

import com.singy.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> { // 泛型：第一个为要存储的数据的类型，第二个为其主键的类型

}
