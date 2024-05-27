import ACCESS_ENUM from "@/access/accessEnum";

/**
 * 检查权限（判断当前登录用户是否有某个权限）
 * @param loginUser 当前登录用户
 * @param needAccess 需要有的权限
 * @return 有无权限
 */
const checkAccess = (loginUser: API.LoginUserVO, needAccess:string = ACCESS_ENUM.NOT_LOGIN) =>{
  // 获取当前登录用户具有的权限（如果没有 loginUser，则表示未登录）
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  if(needAccess === ACCESS_ENUM.NOT_LOGIN){
    return true;
  }
  // 用户登录才能访问
  if(needAccess === ACCESS_ENUM.USER){
    // 如果用户未登录，表示无权限
    if(loginUserAccess === ACCESS_ENUM.NOT_LOGIN){
      return false;
    }
  }
  // 管理员才能访问
  if(needAccess === ACCESS_ENUM.ADMIN){
    // 如果不是管理员，表示无权限
    if(loginUserAccess !== ACCESS_ENUM.ADMIN){
      return false;
    }
  }
  return true;
}

export default checkAccess;