package org.joe.cloud;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.joe.cloud.mapper.UserFileMapper;
import org.joe.cloud.model.entity.UserFile;
import org.joe.cloud.service.UserFileService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class OverCloudApplicationTest {
    @Resource
    private UserFileMapper userFileMapper;
    @Resource
    private UserFileService userFileService;

    @Test
    public void testSQL() {
        UserFile userFile = new UserFile();
        userFileMapper.insert(userFile);
    }

    @Test
    public void testSplit() {
        if (userFileMapper == null) {
            System.out.println("---");
            return;
        }
        List<UserFile> folderList = userFileMapper.selectList(
                new LambdaQueryWrapper<UserFile>()
                        .eq(UserFile::getIsFolder, true)
                        .eq(UserFile::getDeleted, false)
        );
        if (folderList == null) {
            System.out.println("----");
        } else {
            for (UserFile folder : folderList) {
                String filePath = folder.getPath() + folder.getName() + "/";
                System.out.println(filePath);
                String[] route = filePath.split("/");
                for (String r : route) {
                    System.out.println(r);
                }
            }
        }
    }

    @Test
    public void fileExist() {
        System.out.println(userFileService.fileExist("a", null, "/a/d", true));
    }
}
