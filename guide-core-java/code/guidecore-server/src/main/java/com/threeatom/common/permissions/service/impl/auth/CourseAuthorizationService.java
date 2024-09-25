package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitCourse;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.CourseRole;
import com.threeatom.common.permissions.enums.CourseAction;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CourseAuthorizationService
    extends ResourceAuthorizationService<PermitCourse, CourseRole, CourseAction> {

    private static final List<CourseAction> COURSE_ACTIONS = List.of(
        CourseAction.CREATE,
        CourseAction.DELETE,
        CourseAction.VIEW,
        CourseAction.EDIT,
        CourseAction.SUBSCRIBE,
        CourseAction.UNSUBSCRIBE,
        CourseAction.ADD_CONTENT,
        CourseAction.MANAGE_CONTENT,
        CourseAction.PUBLISH
    );
    private static final Map<CourseRole, Map<CourseAction, Boolean>> ROLE_COURSE_PERMISSIONS = Map.of(
        CourseRole.VIEWER, Map.of(
            CourseAction.CREATE, false,
            CourseAction.DELETE, false,
            CourseAction.VIEW, true,
            CourseAction.EDIT, false,
            CourseAction.SUBSCRIBE, true,
            CourseAction.UNSUBSCRIBE, true,
            CourseAction.ADD_CONTENT, false,
            CourseAction.MANAGE_CONTENT, false,
            CourseAction.PUBLISH, false
        ),
        CourseRole.ADMIN, Map.of(
            CourseAction.CREATE, true,
            CourseAction.DELETE, true,
            CourseAction.VIEW, true,
            CourseAction.EDIT, true,
            CourseAction.SUBSCRIBE, true,
            CourseAction.UNSUBSCRIBE, true,
            CourseAction.ADD_CONTENT, true,
            CourseAction.MANAGE_CONTENT, true,
            CourseAction.PUBLISH, true
        )
    );

    @Override
    public CourseRole getRole(PermitUser permitUser, PermitCourse course) {
        if (permitUser.getId().equals(course.getOwnerId())
            || permitUser.isOrgAdmin() && !course.isPrivate()) {
            return CourseRole.ADMIN;
        }

        if (course.isPublic() || userHasCourseInContentGroups(permitUser, course)) {
            return CourseRole.VIEWER;
        }

        return null;
    }

    private boolean userHasCourseInContentGroups(PermitUser permitUser, PermitCourse course) {
        Set<String> allUserContentGroupIds = permitUser.getContentGroupIds();
        allUserContentGroupIds.addAll(permitUser.getManagedContentGroupIds());

        return allUserContentGroupIds.stream()
            .anyMatch(userContentGroupId -> course.getContentGroupIds().contains(userContentGroupId));
    }

    @Override
    protected Map<CourseAction, Boolean> getPermissionMap(CourseRole videoItemRole) {
        return ROLE_COURSE_PERMISSIONS.get(videoItemRole);
    }

    @Override
    protected List<CourseAction> getActions() {
        return COURSE_ACTIONS;
    }
}
