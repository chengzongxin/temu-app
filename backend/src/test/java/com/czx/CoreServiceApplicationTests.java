package com.czx;

import com.czx.mapper.ImageMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@SpringBootTest
class CoreServiceApplicationTests {

    @Autowired
    ImageMapper imageMapper;

    @Test
    void contextLoads() {
    }

    @Test
    public void testUuid(){
        for (int i = 0; i < 1000; i++) {
            String uuid = UUID.randomUUID().toString();
            System.out.println(uuid);
        }
    }

    /**
     * 生成JWT
     */
    @Test
    public void testGenJwt(){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id",1);
        claims.put("name","tom");

        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "eren yeager")//签名算法
                .setClaims(claims) //自定义内容(载荷)
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))//设置有效期为1h
                .compact();
        System.out.println(jwt);
    }

    /**
     * 解析JWT
     */
//    @Test
//    public void testParseJwt(){
//        Claims claims = Jwts.parser()
//                .setSigningKey("eren yeager")
//                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidG9tIiwiaWQiOjEsImV4cCI6MTcwNjA4MDI0N30.IewRSz4oAOlCssruWbb4jMztX9LbRZC-kTkgY6Jh_g8")
//                .getBody();
//        System.out.println(claims);
//    }
//
//    @Test
//    public void testInsertImage(){
//        Image image = new Image();
//        image.setUrl("http://www.bilibili.com");
//        image.setCreateTime(LocalDateTime.now());
//        imageMapper.insert(image);
//    }

}
