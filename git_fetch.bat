@echo off
rem 设置当前控制台为UTF-8编码
chcp 65001 >> nul

git fetch
git rebase origin/master

set "coreFolder=UICore"
IF EXIST "%CD%\%coreFolder%\" (
    cd %coreFolder%
    echo 准备拉取仓库：%coreFolder%
    git fetch
    git rebase origin/master
    cd ..
) else (
    echo 准备克隆仓库：%coreFolder%
    git clone git@gitee.com:angcyo/UICore.git
)

set "extendFolder=UICoreEx"
IF EXIST "%CD%\%extendFolder%\" (
    cd %extendFolder%
    echo 准备拉取仓库：%extendFolder%
    git fetch
    git rebase origin/master
    cd ..
) else (
    echo 准备克隆仓库：%extendFolder%
    git clone git@gitee.com:angcyo/UICoreEx.git
)

echo "结束"
pause