#pragma once

#include <string>

namespace android_mod {

struct HighValueActionResult {
    bool handled = false;
    bool started_async = false;
};

HighValueActionResult executeHighValueAction(
    int feature_num,
    int int_value,
    bool bool_value,
    const std::string &string_value);

bool isDragonEspEnabled();

}
