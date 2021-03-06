# Copyright (C) 2020 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

.class  LTestCase;
.super  LMain;

.method public constructor <init>()V
.registers 2
       invoke-direct {v1}, LMain;-><init>()V
       return-void
.end method

.method public testSuperRange(LTestCase;)I
.registers 8
       invoke-super/range {p1}, LMain;->toInt()I
       move-result v0
       return v0
.end method

.method public testSuper(LTestCase;)I
.registers 8
       invoke-super {p1}, LMain;->toInt()I
       move-result v0
       return v0
.end method

.method public toInt()I
.registers 3
    const v0, 777
    return v0
.end method
