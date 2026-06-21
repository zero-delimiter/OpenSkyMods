#pragma once

#include <cstddef>
#include <cstdint>

namespace android_mod {

struct SwitchCaseTarget {
    int case_id;
    std::uint32_t target_ea;
};

extern const SwitchCaseTarget kPrimarySwitchCases[];
extern const std::size_t kPrimarySwitchCaseCount;
extern const SwitchCaseTarget kCase1500SwitchCases[];
extern const std::size_t kCase1500SwitchCaseCount;
extern const SwitchCaseTarget kFeatureSwitchCases[];
extern const std::size_t kFeatureSwitchCaseCount;

const SwitchCaseTarget *findPrimarySwitchCase(int case_id);
const SwitchCaseTarget *findCase1500SwitchCase(int case_id);
const SwitchCaseTarget *findFeatureSwitchCase(int case_id);

}
