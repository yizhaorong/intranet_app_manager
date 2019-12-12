/**
 * 获取 webHook 列表
 */
function getWebHooks() {
    var appId = $("#appId").val();
    var url = "/webHook/find/" + appId;
    $.post(url, function (result) {
        $(".configrations").children(".config-name").remove();
        var content = "";
        for (var i = 0; i < result.length; i++) {
            content += '<a onclick="editWebHook(this)" class="config-name ng-binding ng-scope"';
            content += 'data="' + result[i].id + '" data-url="' + result[i].url + '" ';
            content += 'data-name="' + result[i].name;
            content += '">#' + result[i].name + '</a>';
        }
        $(".configrations").append(content);
    });
}

/**
 * 编辑 webHook
 * @param e
 */
function editWebHook(e) {
    var id = $("#webHookId").val();
    if (id.length > 0) {
        resetForm();
    } else {
        var name = $(e).attr("data-name");
        var url = $(e).attr("data-url");
        var id = $(e).attr("data");
        $("#webHookId").val(id);
        $("#ding-ding-web-hook-name").val(name);
        $("#ding-ding-web-hook-url").val(url);

        $("#webhook-form-view").removeClass("ng-hide");
        $("#webHookAdd").removeClass("ng-hide");
        $("#webHookUpdate").removeClass("ng-hide");
        $("#webHookRemove").removeClass("ng-hide");
        $("#webHookCancel").removeClass("ng-hide");

        $("#webHookAdd").addClass("ng-hide");
        $("#webHookCancel").addClass("ng-hide");
    }
}

/**
 * 获取包列表
 */
function getPackageList() {
    var appId = $("#appId").val();
    var url = "/packageList/" + appId;
    $.post(url, function (result) {
        if (result.success) {
            var packages = result.packages;
            var packageList = '';
            packageList += '<li>';
            packageList += '<span class="dot"></span>';
            packageList += '<span class="filter ng-binding">版本更新</span>';
            packageList += '<span class="filter version-rollback ng-scope"></span>';
            packageList += '</li>';
            packageList += '<li>';
            packageList += '<div class="market-app-info">';
            packageList += '</div>';
            packageList += '</li>';
            for (var i = 0; i < packages.length; i++) {
                var package = packages[i];
                var version = package.version;
                var buildVersion = package.buildVersion;
                var displayTime = package.displayTime;
                var type = package.type;
                var downloadURL = package.downloadURL;
                var displaySize = package.displaySize;
                var previewURL = package.previewURL;
                var id = package.id;
                var message = package.message;
                packageList += '<li class="package_index_' + id + '">';
                packageList += '<div>';
                packageList += '<div class="directive-view-release">';
                packageList += '<i class="icon-upload-cloud2"></i>';
                packageList += '<b class="ng-binding">' + version + ' (Build ' + buildVersion + ')' + message + '</b > ';
                packageList += '<div class="release-metainfo ng-hide">';
                packageList += '<small><i class="icon-calendar"></i>';
                packageList += '<span class="ng-binding">' + displayTime + '</span>';
                packageList += '</small>';
                packageList += '</div>';
                packageList += '<div class="release-metainfo">';
                packageList += '<small><i class="icon-calendar"></i>';
                packageList += '<span class="ng-binding">' + displayTime + '</span></small> &nbsp;&nbsp;·&nbsp;&nbsp;';
                packageList += '<small class="ng-scope">' + type + '</small>';
                packageList += '<i class="ng-hide">&nbsp;&nbsp;·&nbsp;&nbsp;</i>';
                packageList += '<small class="ng-binding ng-hide"></small>';
                packageList += '</div>';
                packageList += '<div class="release-actions">';
                packageList += '<button class="tooltip-top download-action" tooltip="下载原文件" value="' + downloadURL + '">';
                packageList += '<i class="icon-cloud-download"></i>';
                packageList += '<span class="ng-binding"> ' + displaySize + '</span>';
                packageList += '</button>';
                packageList += '<button class="preview" value="' + previewURL + '">';
                packageList += '<i class="icon-eye"></i>';
                packageList += '<span class="ng-binding"> 预览</span>';
                packageList += '</button>';
                if (i > 0) {
                    packageList += '<button class="ng-scope app-delete" data="' + id + '">';
                    packageList += '<i class="icon-trash"></i>';
                    packageList += '<span class="ng-binding"> 删除</span>';
                    packageList += '</button>';
                }
                packageList += '</div>';
                packageList += '</div >';
                packageList += '</div >';
                packageList += '</li >';
            }
            packageList += '<li class="more ng-hide" ng-show="currentApp.releases.current_page &lt; currentApp.releases.total_pages">';
            packageList += '<button ng-click="moreRelease()" class="ng-binding">显示更多版本</button></li>';
            $("#app-activity-panel").empty();
            $("#app-activity-panel").append(packageList);

            bindActions();
        }
    });

}

/**
 * 构造数据
 * @returns {{appId: (*|jQuery|string|undefined), name: (*|jQuery|string|undefined), id: (*|jQuery|string|undefined), url: (*|jQuery|string|undefined)}}
 */
function buildData() {
    var name = $("#ding-ding-web-hook-name").val();
    var url = $("#ding-ding-web-hook-url").val();
    var appId = $("#appId").val();
    var id = $("#webHookId").val();
    var data = {
        name: name,
        url: url,
        appId: appId,
        id: id
    };
    return data;
}

/**
 * 重置 webHook 表单
 */
function resetForm() {
    $("#ding-ding-web-hook-name").val('');
    $("#ding-ding-web-hook-url").val('');
    $("#webHookId").val('');

    $("#webHookAdd").addClass("ng-hide");
    $("#webHookUpdate").addClass("ng-hide");
    $("#webHookRemove").addClass("ng-hide");
    $("#webHookCancel").addClass("ng-hide");

    $("#webHookAdd").removeClass("ng-hide");
    $("#webHookCancel").removeClass("ng-hide");
    $("#webhook-form-view").addClass("ng-hide");

    $("#webHookUpdate").attr("disabled", "disabled");
    $("#webHookAdd").attr("disabled", "disabled");
}

/**
 * 添加 webHook
 */
function add() {
    postWithURL("/webHook/add")
}

/**
 * 更新 webHook
 */
function update() {
    postWithURL("/webHook/update")
}

/**
 * 删除 webHook
 */
function remove() {
    postWithURL("/webHook/delete")
}

/**
 * 发送请求
 */
function postWithURL(url) {
    var data = buildData();
    resetForm();
    $.post(url, data, function (result) {
        getWebHooks();
    });
}

/**
 * 切换面板时样式清除
 */
function removeAllPanelClass() {
    $("#info-container").removeClass("app-info");
    $("#info-container").removeClass("app-integration");
    $("#info-container").removeClass("app-activities");
    $("#info-panel").removeClass("apps-app-integration")
    $("#info-panel").removeClass("apps-app-info");
    $("#info-panel").removeClass("apps-app-activities");
    $("#app-activity-panel").removeClass("ng-hide");
    $("#app-info-panel").removeClass("ng-hide");
    $("#app-integration-panel").removeClass("ng-hide");
}

/**
 * 绑定事件
 */
function bindActions() {
    $(".download-action").click(function () {
        window.open($(this).val())
    });

    $(".preview").click(function () {
        window.open($(this).val())
    });

    $(".app-delete").click(function () {
        var id = $(this).attr("data");
        var url = "/p/delete/" + id;
        var li = "package_index_" + id;
        console.log(li);
        var self = $("." + li);
        $.post(url, function (result) {
            if (result.success) {
                self.remove();
            }
        });
    });
}

$(function () {
    getPackageList();
    getWebHooks();

    $("#js-app-short-copy-trigger").click(function () {
        new ClipboardJS('#js-app-short-copy-trigger', {
            text: function (trigger) {
                return trigger.getAttribute('value');
            }
        });
    });

    $("#app-activity-icon").click(function () {
        removeAllPanelClass();
        $("#info-container").addClass("app-activities");
        $("#info-panel").addClass("apps-app-activities");
        $("#app-info-panel").addClass("ng-hide");
        $("#app-integration-panel").addClass("ng-hide");
    });
    $("#app-info-icon").click(function () {
        removeAllPanelClass();
        $("#info-container").addClass("app-info");
        $("#info-panel").addClass("apps-app-info");
        $("#app-activity-panel").addClass("ng-hide");
        $("#app-integration-panel").addClass("ng-hide");
    });
    $("#app-integration-icon").click(function () {
        removeAllPanelClass();
        $("#info-container").addClass("app-integration");
        $("#info-panel").addClass("apps-app-integration");
        $("#app-activity-panel").addClass("ng-hide");
        $("#app-info-panel").addClass("ng-hide");
    });

    $("#delete-app").click(function () {
        var url = "/app/delete/" + $(this).attr("data");
        $.post(url, function (result) {
            window.location.href = "/apps"
        });
    });

    $("#ding-ding-web-hook-name, #ding-ding-web-hook-url").bind("input propertychange", function (event) {
        var name = $("#ding-ding-web-hook-name").val();
        var url = $("#ding-ding-web-hook-url").val();
        if (name.length > 0 && url.length > 0) {
            $("#webHookAdd").removeAttr("disabled");
            $("#webHookUpdate").removeAttr("disabled");
        }
    });

    $("#webHookCancel").click(function () {
        resetForm();
    });

    $(".add-config").click(function () {
        resetForm();
        $("#webhook-form-view").removeClass("ng-hide");
    });
});