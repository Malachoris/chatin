package com.chatin.microbloggingappspringboot.config;

import com.chatin.microbloggingappspringboot.dto.SignUpDto;
import com.chatin.microbloggingappspringboot.models.Authority;
import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.repositories.AuthorityRepository;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import com.chatin.microbloggingappspringboot.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private PostService postService;

    @Autowired
    private BloggerService bloggerService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public void run(String... args) throws Exception {

        List<Post> posts = postService.getAll();

        if (posts.size() == 0) {

            Authority user = new Authority();
            user.setName("ROLE_USER");
            authorityRepository.save(user);

            Authority admin = new Authority();
            admin.setName("ROLE_ADMIN");
            authorityRepository.save(admin);

            Blogger account1 = Blogger
                    .builder()
                    .firstName("user_first")
                    .username("user_last")
                    .email("user.user@domain.com")
                    .password("password")
                    .build();

            SignUpDto signUpDto = new SignUpDto();
            signUpDto.setFirstName("user_first");
            signUpDto.setUsername("user_last");
            signUpDto.setEmail("user.user@domain.com");
            signUpDto.setPassword("password");

            SignUpDto signUpDto1 = new SignUpDto();
            signUpDto1.setFirstName("admin_first");
            signUpDto1.setUsername("admin_last");
            signUpDto1.setEmail("admin.admin@domain.com");
            signUpDto1.setPassword("password");

//            Set<Authority> authorities1 = new HashSet<>();
//            authorityRepository.findById("ROLE_USER").ifPresent(authorities1::add);
//            account1.setAuthorities(authorities1);

            Set<Authority> authorities2 = new HashSet<>();
            authorityRepository.findById("ROLE_ADMIN").ifPresent(authorities2::add);
            authorityRepository.findById("ROLE_USER").ifPresent(authorities2::add);

            Blogger account2 = Blogger
                    .builder()
                    .firstName("admin_first")
                    .username("admin_last")
                    .email("admin.admin@domain.com")
                    .password("password")
                    .authorities(authorities2)
                    .build();

            account2.setAuthorities(authorities2);

//            Blogger account1;
//            Blogger account2;
//            account1 = bloggerService.save(signUpDto);
//            account2 = bloggerService.save(signUpDto1);

            bloggerService.saveSeed(account1);
            bloggerService.saveSeed(account2);
            System.out.println(account2.getAuthorities());
            System.out.println(account1.getAuthorities());

//            bloggerService.addRoleToBlogger(account1, "ROLE_ADMIN");
//            bloggerService.deleteRoleFromBlogger(account1, "ROLE_USER");
//            bloggerService.deleteRoleFromBlogger(account1, "ROLE_ADMIN");

//            bloggerService.saveUpdate(signUpDto);
//            bloggerService.saveUpdate(signUpDto1);

            Post post1 = Post
                    .builder()
                    .title("What is Lorem Ipsum?")
                    .body("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                    .blogger(account1)
                    .build();

            Post post2 = Post
                    .builder()
                    .title("Something else Ipsum")
                    .body("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna eget est lorem ipsum dolor sit amet consectetur adipiscing. Tempus quam pellentesque nec nam aliquam sem et tortor. Pellentesque sit amet porttitor eget. Sed augue lacus viverra vitae congue eu consequat. Ultrices vitae auctor eu augue. Mattis rhoncus urna neque viverra. Consectetur lorem donec massa sapien faucibus et molestie ac feugiat. Sociis natoque penatibus et magnis dis parturient montes nascetur. Cursus turpis massa tincidunt dui ut ornare lectus. Odio pellentesque diam volutpat commodo sed egestas egestas fringilla. Id cursus metus aliquam eleifend mi. Nibh nisl condimentum id venenatis a condimentum.")
                    .blogger(account2)
                    .build();

            postService.save(post1);
            postService.save(post2);
        }
    }
}