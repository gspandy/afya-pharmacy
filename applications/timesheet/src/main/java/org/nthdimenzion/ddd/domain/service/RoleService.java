package org.nthdimenzion.ddd.domain.service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.nthdimenzion.crud.ICrud;
import org.nthdimenzion.ddd.domain.model.PersonRole;
import org.nthdimenzion.ddd.domain.sharedkernel.DomainRole;
import org.nthdimenzion.ddd.infrastructure.LoggedInUserHolder;
import org.nthdimenzion.presentation.LoggedInUserFilter;
import org.nthdimenzion.security.domain.IUserLoginRepository;
import org.nthdimenzion.security.domain.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by IntelliJ IDEA.
 * User: Nthdimenzion
 * Date: 10/4/13
 * Time: 11:19 PM
 */
@Service
public class RoleService {

    @Autowired
    private ICrud crudDao;

    @Autowired
    private IUserLoginRepository userLoginRepository;

    Function<String,Class> findDomainRole = null;

    public <T> T getRolePlayedByUser(String username)  {
        UserLogin userLogin = userLoginRepository.findUserLoginWithUserName(username);
        PersonRole personRole = userLogin.getPersonRole();
        Preconditions.checkNotNull(personRole);
        Class clazz = findDomainRole.apply(personRole.getDomainRole());
        return (T) crudDao.find(clazz, personRole.getId());
    }


    public <T> T getRolePlayedByCurrentUser(){
        return getRolePlayedByUser(LoggedInUserHolder.getUserName());
    }

    @Autowired(required = false)
    public void setFindDomainRole(Function<String, Class> findDomainRole) {
        this.findDomainRole = findDomainRole;
    }


}
