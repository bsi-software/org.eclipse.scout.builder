:##############################################################################
:# Copyright (c) 2010 BSI Business Systems Integration AG.
:# All rights reserved. This program and the accompanying materials
:# are made available under the terms of the Eclipse Public License v1.0
:# which accompanies this distribution, and is available at
:# http://www.eclipse.org/legal/epl-v10.html
:# 
:# Contributors:
:#     BSI Business Systems Integration AG - initial API and implementation
:#############################################################################

@echo off
setlocal
set JAVA_HOME=E:\jsdk\j2sdk1.6.0_10
set ANT_HOME=E:\jsdk\apache-ant-1.7.1
set ANT_OPTS=-Xmx512m
set WORKSPACE=E:\workspaces\scout.build\p2Workspace

cd %WORKSPACE%

:# standard values for Eclipse 3.5 Classic SDK + Delta Pack 3.5
set buildOpts=-Declipse.running=true 
:# set workspaceDir=-Dworkspace=%WORKSPACE%
set nighltyRepoVar=-DnightlyRepository=E:\workspaces\scout.build\p2Workspace\org.eclipse.scout.releng\final\nightlyP2Repo
:# productiv : nightlyRepository=/home/data/httpd/download.eclipse.org/scout/updates/3.5.6-nightly 


:# create a log file named according to this pattern: log.<this shell sctipt name, i.e. make>
set logfile=org.eclipse.scout.releng/scoutBuild.log

PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%


call ant -f org.eclipse.scout.releng/buildFiles/build.xml  -lib org.eclipse.scout.releng/buildFiles/lib %buildOpts%  %nighltyRepoVar% %* build 
:# call ant -f org.eclipse.scout.builder/buildFiles/build.xml -lib org.eclipse.scout.releng %buildOpts%  %nighltyRepoVar% -DskipSign=true %* buildNightly  > %logfile%

endlocal
pause


