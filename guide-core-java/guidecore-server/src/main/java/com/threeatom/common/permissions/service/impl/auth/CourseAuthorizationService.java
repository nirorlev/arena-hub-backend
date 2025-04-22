package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitCourse;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.CourseAction;
import com.threeatom.common.permissions.enums.CourseRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
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
        CourseAction.PUBLISH,
        CourseAction.RATE
    );
    private static final Map<CourseRole, Map<CourseAction, Predicate<PermitCourse>>> ROLE_COURSE_PERMISSIONS = Map.of(
        CourseRole.VIEWER, Map.of(
            CourseAction.CREATE, course -> false,
            CourseAction.DELETE, course -> false,
            CourseAction.VIEW, course -> true,
            CourseAction.EDIT, course -> false,
            CourseAction.SUBSCRIBE, course -> true,
            CourseAction.UNSUBSCRIBE, course -> true,
            CourseAction.ADD_CONTENT, course -> false,
            CourseAction.MANAGE_CONTENT, course -> false,
            CourseAction.PUBLISH, course -> false,
            CourseAction.RATE, course -> true
        ),
        CourseRole.ADMIN, Map.of(
            CourseAction.CREATE, course -> true,
            CourseAction.DELETE, course -> true,
            CourseAction.VIEW, course -> true,
            CourseAction.EDIT, course -> true,
            CourseAction.SUBSCRIBE, course -> true,
            CourseAction.UNSUBSCRIBE, course -> true,
            CourseAction.ADD_CONTENT, course -> true,
            CourseAction.MANAGE_CONTENT, course -> true,
            CourseAction.PUBLISH, course -> true,
            CourseAction.RATE, course -> true
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
    protected Map<CourseAction, Predicate<PermitCourse>> getPermissionMap(CourseRole videoItemRole) {
        return ROLE_COURSE_PERMISSIONS.get(videoItemRole);
    }

    @Override
    protected List<CourseAction> getActions() {
        return COURSE_ACTIONS;
    }
}
