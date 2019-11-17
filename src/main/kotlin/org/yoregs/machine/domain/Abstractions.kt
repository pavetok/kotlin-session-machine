package org.yoregs.machine.domain

interface Endpoint
interface Role
interface Choice
interface Variable

class LinearVariable : Variable

class InternalChoice<T> : Endpoint
class ExternalChoice<T> : Endpoint
class Lolly<T> : Endpoint
class Tensor<T> : Endpoint
