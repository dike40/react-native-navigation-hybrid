import NavigationModule from './NavigationModule';
import { NativeModules } from 'react-native';
const GardenModule = NativeModules.GardenHybrid;

let intercept;

export default class Navigator {
  static setRoot(layout, sticky = false) {
    NavigationModule.setRoot(layout, sticky);
  }

  static setInterceptor(interceptor) {
    intercept = interceptor;
  }

  constructor(sceneId, moduleName) {
    this.sceneId = sceneId;
    this.moduleName = moduleName;
    this.dispatch = this.dispatch.bind(this);
    this.setParams = this.setParams.bind(this);
    this.setTabBadge = this.setTabBadge.bind(this);
    this.setMenuInteractive = this.setMenuInteractive.bind(this);

    this.push = this.push.bind(this);
    this.pop = this.pop.bind(this);
    this.popTo = this.popTo.bind(this);
    this.popToRoot = this.popToRoot.bind(this);
    this.replace = this.replace.bind(this);
    this.replaceToRoot = this.replaceToRoot.bind(this);
    this.isRoot = this.isRoot.bind(this);

    this.present = this.present.bind(this);
    this.dismiss = this.dismiss.bind(this);
    this.showModal = this.showModal.bind(this);
    this.hideModal = this.hideModal.bind(this);
    this.setResult = this.setResult.bind(this);

    this.switchToTab = this.switchToTab.bind(this);
    this.toggleMenu = this.toggleMenu.bind(this);
    this.openMenu = this.openMenu.bind(this);
    this.closeMenu = this.closeMenu.bind(this);
  }

  state = { params: {} };

  // 向后兼容， 1.0.0 将删除
  setTabBadge(index, text) {
    GardenModule.setTabBadge(this.sceneId, index, text);
  }

  // 向后兼容， 1.0.0 将删除
  setMenuInteractive(enabled) {
    GardenModule.setMenuInteractive(this.sceneId, enabled);
  }

  setParams(params = {}) {
    this.state.params = { ...this.state.params, ...params };
  }

  dispatch(action, extras = {}) {
    if (!intercept || !intercept(action, this.moduleName, extras.moduleName, extras)) {
      NavigationModule.dispatch(this.sceneId, action, extras);
    }
  }

  push(moduleName, props = {}, options = {}, animated = true) {
    this.dispatch('push', { moduleName, props, options, animated });
  }

  pop(animated = true) {
    this.dispatch('pop', { animated });
  }

  popTo(sceneId, animated = true) {
    this.dispatch('popTo', { animated, targetId: sceneId });
  }

  popToRoot(animated = true) {
    this.dispatch('popToRoot', { animated });
  }

  replace(moduleName, props = {}, options = {}) {
    this.dispatch('replace', { moduleName, props, options, animated: true });
  }

  replaceToRoot(moduleName, props = {}, options = {}) {
    this.dispatch('replaceToRoot', { moduleName, props, options, animated: true });
  }

  isRoot() {
    return NavigationModule.isNavigationRoot(this.sceneId);
  }

  present(moduleName, requestCode = 0, props = {}, options = {}, animated = true) {
    this.dispatch('present', {
      moduleName,
      props,
      options,
      requestCode,
      animated,
    });
  }

  dismiss(animated = true) {
    this.dispatch('dismiss', { animated });
  }

  showModal(moduleName, requestCode = 0, props = {}, options = {}) {
    this.dispatch('showModal', {
      moduleName,
      props,
      options,
      requestCode,
    });
  }

  hideModal() {
    this.dispatch('hideModal');
  }

  setResult(resultCode, data = {}) {
    NavigationModule.setResult(this.sceneId, resultCode, data);
  }

  switchToTab(index) {
    this.dispatch('switchToTab', { index });
  }

  toggleMenu() {
    this.dispatch('toggleMenu');
  }

  openMenu() {
    this.dispatch('openMenu');
  }

  closeMenu() {
    this.dispatch('closeMenu');
  }

  signalFirstRenderComplete() {
    NavigationModule.signalFirstRenderComplete(this.sceneId);
  }
}
