import { createStore } from 'vuex'

const USER = "USER";

// 封装sessionStorage操作方法，避免直接使用可能导致的错误
const sessionStorageService = {
  get: function(key) {
    try {
      const value = sessionStorage.getItem(key);
      return value ? JSON.parse(value) : null;
    } catch (e) {
      console.error('获取sessionStorage失败:', e);
      return null;
    }
  },
  set: function(key, value) {
    try {
      sessionStorage.setItem(key, JSON.stringify(value));
    } catch (e) {
      console.error('设置sessionStorage失败:', e);
    }
  }
};

const store = createStore({
  state() {
    return {
      user: sessionStorageService.get(USER) || {}
    }
  },
  mutations: {
    setUser(state, user) {
      console.log("store user：", user);
      state.user = user;
      sessionStorageService.set(USER, user);
    }
  },
  actions: {
  },
  modules: {
  }
});

export default store;