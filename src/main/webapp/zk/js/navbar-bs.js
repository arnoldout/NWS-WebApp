zk.afterLoad('zul.wgt', function () {
    var _navbar = {};
 
zk.override(zul.wgt.navbar.prototype, _navbar, {
    _sclass: 'navbar-default',
    getZclass: function () {
        return 'navbar';
    }
});
 
});