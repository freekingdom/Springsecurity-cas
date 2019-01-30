package com.example.demo.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dao.MyUserDao;
import com.example.demo.dao.PermissionDao;
import com.example.demo.entity.Permission;


@Service
public class CustomUserService implements AuthenticationUserDetailsService {
	
	@Autowired
    private MyUserDao myUserDao;
	@Autowired
    private PermissionDao permissionDao;

/*	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		SysUser user = myUserDao.findByUserName(username);
        if (user != null) {
            List<Permission> permissions = permissionDao.findByAdminUserId(user.getId());
            List<GrantedAuthority> grantedAuthorities = new ArrayList <>();
            for (Permission permission : permissions) {
                if (permission != null && permission.getName()!=null) {

                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
                //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
                grantedAuthorities.add(grantedAuthority);
                }
            }
            return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("admin: " + username + " do not exist!");
        }
	}*/

	@Override
	public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Assertion assertion=((CasAssertionAuthenticationToken)token).getAssertion();
        Map<String,Object> attributes=assertion.getPrincipal().getAttributes();
        String pwd = attributes.get("pwd").toString();
        String userName = attributes.get("userUid").toString();
       List<Permission> permissions = permissionDao.findByAdminUserId(userName);
        List<GrantedAuthority> grantedAuthorities = new ArrayList <>();
        for (Permission permission : permissions) {
            if (permission != null && permission.getName()!=null) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
            //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
            grantedAuthorities.add(grantedAuthority);
            }
        }

        org.springframework.security.core.userdetails.User auth_user = new 
        		org.springframework.security.core.userdetails.User(userName,pwd,grantedAuthorities);
        return auth_user ;
	}

}
