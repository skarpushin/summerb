package integr.org.summerb.easycrud;

import integr.org.summerb.easycrud.dtos.CommentRow;
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import integr.org.summerb.easycrud.dtos.UserStatus;
import integr.org.summerb.easycrud.testbeans.PostRowService;
import integr.org.summerb.easycrud.testbeans.UserRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.summerb.easycrud.EasyCrudService;

public class JoinQueryTestAbstract {
  @Autowired
  @Qualifier("commentRowService")
  protected EasyCrudService<Long, CommentRow> commentRowService;

  @Qualifier("postRowServiceCustom")
  @Autowired
  protected PostRowService postRowService;

  @Autowired protected UserRowService userRowService;
  @Autowired Environment environment;

  protected boolean isPostgres() {
    return environment.matchesProfiles("postgres");
  }

  protected void createTestData() {
    UserRow user1 = new UserRow();
    user1.setName("bba");
    user1.setKarma(5);
    user1.setStatus(UserStatus.ACTIVE);
    user1 = userRowService.create(user1);

    UserRow user2 = new UserRow();
    user2.setName("BBc");
    user2.setKarma(10);
    user2 = userRowService.create(user2);

    UserRow user3 = new UserRow();
    user3.setName("name3");
    user3.setKarma(15);
    user3.setStatus(UserStatus.INACTIVE);
    user3 = userRowService.create(user3);

    PostRow post1 = new PostRow();
    post1.setTitle("env5");
    post1.setLikes(5);
    post1.setBody("link1");
    post1.setAuthorId(user1.getId());
    post1.setPinnedBy(user2.getId());
    post1 = postRowService.create(post1);

    PostRow post2 = new PostRow();
    post2.setTitle("env4");
    post2.setLikes(4);
    post2.setBody("link11");
    post2.setAuthorId(user1.getId());
    post2.setPinnedBy(user3.getId());
    post2 = postRowService.create(post2);

    PostRow post3 = new PostRow();
    post3.setTitle("env3");
    post3.setLikes(3);
    post3.setBody("link2");
    post3.setAuthorId(user2.getId());
    post3 = postRowService.create(post3);

    CommentRow com1 = new CommentRow();
    com1.setComment("AAA");
    com1.setPostId(post1.getId());
    com1.setAuthorId(user3.getId());
    commentRowService.create(com1);

    CommentRow com2 = new CommentRow();
    com2.setComment("BBB");
    com2.setPostId(post2.getId());
    com2.setAuthorId(user3.getId());
    commentRowService.create(com2);

    CommentRow com3 = new CommentRow();
    com3.setComment("CCC");
    com3.setPostId(post2.getId());
    com3.setAuthorId(user3.getId());
    commentRowService.create(com3);

    CommentRow com4 = new CommentRow();
    com4.setComment("DDD");
    com4.setPostId(post3.getId());
    com4.setAuthorId(user3.getId());
    commentRowService.create(com4);
  }
}
