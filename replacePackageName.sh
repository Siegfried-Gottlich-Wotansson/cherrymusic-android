#!/bin/bash
#########################################################
#
#   Package name replacer
#
#   Created by Gregor Santer (gsantner), 2017
#   https://gsantner.github.io/
#
#########################################################


#Pfade
SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SCRIPTFILE=$(readlink -f $0)
SCRIPTPATH=$(dirname $SCRIPTFILE)
SCRIPTDIRPARENT="$(dirname "$SCRIPTDIR")"
argc=$#

#########################################################
cd "$SCRIPTPATH"

packageName=`cat wawl/AndroidManifest.xml | grep "package=" | cut -d '"' -f 2 | sed 's#\.#\\\.#g'`
find wawl app -type f -iname "*.java" -exec sed -i "s/import .*\.R;/import $packageName.R;/g" -i "{}" \;
find wawl app -type f -iname "*.java" -exec sed -i "s/import .*\.BuildConfig;/import $packageName.BuildConfig;/g" -i "{}" \;
